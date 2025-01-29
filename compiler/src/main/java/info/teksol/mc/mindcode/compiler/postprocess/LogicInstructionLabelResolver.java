package info.teksol.mc.mindcode.compiler.postprocess;

import info.teksol.mc.mindcode.compiler.InstructionCounter;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.SortCategory;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
public class LogicInstructionLabelResolver {
    private final CompilerProfile profile;
    private final InstructionProcessor processor;

    private final Map<LogicLabel, LogicLabel> addresses = new HashMap<>();

    public LogicInstructionLabelResolver(CompilerProfile profile, InstructionProcessor processor) {
        this.processor = processor;
        this.profile = profile;
    }
    
    public static List<LogicInstruction> resolve(CompilerProfile profile, InstructionProcessor processor,
            List<LogicInstruction> program) {
        return new LogicInstructionLabelResolver(profile, processor).resolve(program);
    }

    private List<LogicInstruction> resolve(List<LogicInstruction> program) {
        return sortVariables(resolveLabels(program));
    }

    public List<LogicInstruction> sortVariables(List<LogicInstruction> program) {
        if (profile.getSortVariables().isEmpty() || program.isEmpty()) {
            return program;
        }

        HashSet<LogicArgument> allVariables = program.stream()
                .flatMap(LogicInstruction::inputOutputArgumentsStream)
                .filter(a -> a.isUserVariable() || a.getType() == ArgumentType.BLOCK)
                .collect(Collectors.toCollection(HashSet::new));

        List<LogicArgument> order = orderVariables(allVariables, profile.getSortVariables());

        while (order.size() % 4 > 0) {
            order.add(LogicNull.NULL);
        }

        int limit = profile.getInstructionLimit() - InstructionCounter.computeSize(program);
        if (limit <= 0) {
            return program;
        }

        int instructions = Math.min(limit, order.size() / 4);

        List<LogicInstruction> result = new ArrayList<>();
        AstContext astContext = program.getFirst().getAstContext();
        for (int index = 0, i = 0; i++ < instructions; index += 5) {
            order.add(index, LogicVariable.unusedVariable());
            result.add(processor.createInstruction(astContext,  PACKCOLOR, order.subList(index, index + 5)));
        }

        result.addAll(program);
        return result;
    }

    public static List<LogicArgument> orderVariables(Set<LogicArgument> allVariables, List<SortCategory> categories) {
        // Sort all categories except ALL
        Map<SortCategory, List<LogicArgument>> sorted = new EnumMap<>(SortCategory.class);
        for (SortCategory category : categories) {
            if (category != SortCategory.ALL) {
                List<LogicArgument> selected = allVariables.stream()
                        .filter(v -> matches(v, category))
                        .sorted(Comparator.comparing(LogicArgument::toMlog))
                        .toList();

                sorted.put(category, selected);
                selected.forEach(allVariables::remove);
            }
        }

        // What remains is ALL
        sorted.put(SortCategory.ALL, allVariables.stream().sorted(Comparator.comparing(LogicArgument::toMlog)).toList());

        // Now put categories in the proper order
        List<LogicArgument> order = new ArrayList<>();
        for (SortCategory category : categories) {
            List<LogicArgument> variables = sorted.remove(category);
            if (variables != null) {
                order.addAll(variables);
            }
        }

        return order;
    }

    private static boolean matches(LogicArgument logicVariable, SortCategory category) {
        return switch (category) {
            case LINKED     -> logicVariable.getType() == ArgumentType.BLOCK;
            case PARAMS     -> logicVariable.getType() == ArgumentType.PARAMETER;
            case GLOBALS    -> logicVariable.getType() == ArgumentType.GLOBAL_VARIABLE;
            case MAIN       -> logicVariable.isMainVariable();
            case LOCALS     -> logicVariable.isLocalVariable();
            case NONE       -> false;
            case ALL        -> true;
        };
    }

    public List<LogicInstruction> resolveLabels(List<LogicInstruction> program) {
        if (program.isEmpty()) {
            return program;
        }

        // Expand jump tables
        program = expandJumpTables(program);

        // Save the last instruction before it is resolved
        LogicInstruction last = program.getLast();

        program = resolveRemarks(program);
        calculateAddresses(program);
        program = resolveAddresses(resolveVirtualInstructions(program));

        if (last.endsCodePath() && profile.isSignature() && program.size() < profile.getInstructionLimit()) {
            program.add(processor.createPrint(last.getAstContext(),
                    LogicString.create(CompilerProfile.SIGNATURE)));
        }

        return program;
    }

    private List<LogicInstruction> expandJumpTables(List<LogicInstruction> program) {
        // Read tables
        Map<String, List<LogicInstruction>> readJumpTables = program.stream()
                .filter(ReadArrInstruction.class::isInstance)
                .map(ReadArrInstruction.class::cast)
                .map(ArrayAccessInstruction::getArray)
                .distinct()
                .collect(Collectors.toMap(LogicArray::getReadJumpTableId, this::buildReadJumpTable));

        // Write tables
        Map<String, List<LogicInstruction>> writeJumpTables = program.stream()
                .filter(WriteArrInstruction.class::isInstance)
                .map(WriteArrInstruction.class::cast)
                .map(ArrayAccessInstruction::getArray)
                .distinct()
                .collect(Collectors.toMap(LogicArray::getWriteJumpTableId, this::buildWriteJumpTable));

        Map<String, List<LogicInstruction>> jumpTables = new HashMap<>(readJumpTables);
        jumpTables.putAll(writeJumpTables);

        List<LogicInstruction> expanded = program.stream()
                .mapMulti((LogicInstruction ix, Consumer<LogicInstruction> consumer)
                        -> expandInstruction(ix, jumpTables, consumer))
                .collect(Collectors.toCollection(ArrayList::new));

        readJumpTables.values().forEach(expanded::addAll);
        writeJumpTables.values().forEach(expanded::addAll);

        return expanded;
    }

    private List<LogicInstruction> buildReadJumpTable(LogicArray array) {
        AstContext astContext = MindcodeCompiler.getContext().getRootAstContext()
                .createSubcontext(AstContextType.ARRAY_READ, AstSubcontextType.BASIC, 1.0);
        List<LogicInstruction> result = new ArrayList<>();
        LogicLabel marker = processor.nextMarker();

        result.add(processor.createEnd(astContext));
        for (LogicVariable element : array.getElements()) {
            result.add(processor.createMultiLabel(astContext, processor.nextLabel(), marker));
            result.add(processor.createSet(astContext, array.readVal, element));
            result.add(processor.createReturn(astContext, array.readRet));
        }

        return result;
    }

    private List<LogicInstruction> buildWriteJumpTable(LogicArray array) {
        AstContext astContext = MindcodeCompiler.getContext().getRootAstContext()
                .createSubcontext(AstContextType.ARRAY_WRITE, AstSubcontextType.BASIC, 1.0);
        List<LogicInstruction> result = new ArrayList<>();
        LogicLabel marker = processor.nextMarker();

        result.add(processor.createEnd(astContext));
        for (LogicVariable element : array.getElements()) {
            result.add(processor.createMultiLabel(astContext, processor.nextLabel(), marker));
            result.add(processor.createSet(astContext, element, array.writeVal));
            result.add(processor.createReturn(astContext, array.writeRet));
        }

        return result;
    }

    private void expandInstruction(LogicInstruction instruction, Map<String, List<LogicInstruction>> jumpTables, Consumer<LogicInstruction> consumer) {
        if (!(instruction instanceof ArrayAccessInstruction ix)) {
            consumer.accept(instruction);
            return;
        }

        AstContext astContext = ix.getAstContext();
        LogicArray array = ix.getArray();
        LogicVariable temp = processor.nextTemp();
        LogicLabel marker = Objects.requireNonNull(jumpTables.get(ix.getJumpTableId()).get(1).getMarker());
        LogicLabel target = ((LabeledInstruction) jumpTables.get(ix.getJumpTableId()).get(1)).getLabel();
        LogicLabel returnLabel = processor.nextLabel();

        switch (instruction) {
            case ReadArrInstruction rix -> {
                consumer.accept(processor.createSetAddress(astContext, array.readRet, returnLabel));
                consumer.accept(processor.createOp(astContext, Operation.MUL, temp, ix.getIndex(), LogicNumber.TWO));
                consumer.accept(processor.createMultiJump(astContext, target, temp, LogicNumber.ZERO, marker));
                consumer.accept(processor.createMultiLabel(astContext, returnLabel, marker));
                consumer.accept(processor.createSet(astContext, rix.getResult(), array.readVal));
            }

            case WriteArrInstruction wix -> {
                consumer.accept(processor.createSetAddress(astContext, array.writeRet, returnLabel));
                consumer.accept(processor.createSet(astContext, array.writeVal, wix.getValue()));
                consumer.accept(processor.createOp(astContext, Operation.MUL, temp, ix.getIndex(), LogicNumber.TWO));
                consumer.accept(processor.createMultiJump(astContext, target, temp, LogicNumber.ZERO, marker));
                consumer.accept(processor.createMultiLabel(astContext, returnLabel, marker));
            }

            default -> consumer.accept(instruction);
        }
    }

    private List<LogicInstruction> resolveRemarks(List<LogicInstruction> program) {
        return switch (profile.getRemarks()) {
            case ACTIVE     -> resolveRemarksActive(program);
            case NONE       -> resolveRemarksNone(program);
            case PASSIVE    -> resolveRemarksPassive(program);
        };
    }

    private List<LogicInstruction> resolveRemarksActive(List<LogicInstruction> program) {
        return program.stream()
                .map(ix -> ix instanceof RemarkInstruction r ? processor.createPrint(r.getAstContext(), r.getValue()) : ix)
                .toList();
    }

    private List<LogicInstruction> resolveRemarksNone(List<LogicInstruction> program) {
        return program.stream()
                .filter(ix -> !(ix instanceof RemarkInstruction))
                .toList();
    }

    private List<LogicInstruction> resolveRemarksPassive(List<LogicInstruction> program) {
        List<LogicInstruction> result = new ArrayList<>();
        LabelInstruction activeLabel = null;
        for (LogicInstruction ix : program) {
            if (ix instanceof RemarkInstruction) {
                if (activeLabel == null) {
                    LogicLabel label = processor.nextLabel();
                    activeLabel = processor.createLabel(ix.getAstContext(),label);
                    result.add(processor.createJumpUnconditional(ix.getAstContext(), label));
                }
                result.add(processor.createPrint(ix.getAstContext(), ((RemarkInstruction) ix).getValue()));
            } else {
                if (activeLabel != null) {
                    result.add(activeLabel);
                    activeLabel = null;
                }
                result.add(ix);
            }
        }

        if (activeLabel != null) {
            result.add(activeLabel);
            result.add(processor.createEnd(activeLabel.getAstContext()));
        }
        return result;
    }

    private void calculateAddresses(List<LogicInstruction> program) {
        int instructionPointer = 0;
        for (int i = 0; i < program.size(); i++) {
            final LogicInstruction instruction = program.get(i);
            instructionPointer += instruction.getRealSize(null);
            if (instruction instanceof LabeledInstruction ix) {
                if (addresses.containsKey(ix.getLabel())) {
                    throw new MindcodeInternalError("Duplicate label detected: '%s' reused at least twice in %s", ix.getLabel(), program);
                }

                addresses.put(ix.getLabel(), LogicLabel.absolute(instructionPointer));
            }
        }
    }

    private LogicArgument resolveLabel(LogicArgument argument) {
        if (argument instanceof LogicLabel label) {
            if (!addresses.containsKey(label)) {
                throw new MindcodeInternalError("Unknown jump label target: '%s' was not previously discovered in program.", label);
            }
            return addresses.get(label);
        } else {
            return argument;
        }
    }

    private List<LogicInstruction> resolveAddresses(List<LogicInstruction> program) {
        final List<LogicInstruction> result = new ArrayList<>();

        for (final LogicInstruction instruction : program) {
            if (instruction instanceof MultiJumpInstruction ix) {
                if (ix.getTarget() instanceof LogicVariable var) {
                    LogicInstruction newInstruction = processor.createInstruction(ix.getAstContext(),
                            SET, LogicBuiltIn.COUNTER, var);
                    result.add(newInstruction);
                } else if (resolveLabel(ix.getTarget()) instanceof LogicLabel label && label.getAddress() >= 0) {
                    int offset = label.getAddress() - ix.getOffset().getIntValue();
                    LogicInstruction newInstruction = processor.createInstruction(ix.getAstContext(),
                            OP, Operation.ADD, LogicBuiltIn.COUNTER, ix.getValue(), LogicNumber.create(offset));
                    result.add(newInstruction);
                } else {
                    throw new MindcodeInternalError("MultiJump target '%s' is not a label.", ix.getTarget());
                }
            } else if (instruction.getArgs().stream().anyMatch(a -> a.getType() == ArgumentType.LABEL)) {
                List<LogicArgument> newArgs = instruction.getArgs().stream().map(this::resolveLabel).toList();
                LogicInstruction newInstruction = processor.replaceArgs(instruction, newArgs);
                result.add(newInstruction);
            } else {
                result.add(instruction);
            }
        }
        return result;
    }

    private List<LogicInstruction> resolveVirtualInstructions(List<LogicInstruction> program) {
        return program.stream().mapMulti(processor::resolve).toList();
    }
}
