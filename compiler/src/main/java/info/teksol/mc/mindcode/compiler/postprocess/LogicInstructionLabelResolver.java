package info.teksol.mc.mindcode.compiler.postprocess;

import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.InstructionCounter;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
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
    private final AstContext rootAstContext;

    private final Map<LogicLabel, LogicInstruction> labelInstructions = new HashMap<>();
    private final Map<LogicLabel, LogicLabel> addresses = new HashMap<>();
    private final Map<Integer, AstContext> addressContexts = new HashMap<>();

    public LogicInstructionLabelResolver(CompilerProfile profile, InstructionProcessor processor, AstContext rootAstContext) {
        this.processor = processor;
        this.profile = profile;
        this.rootAstContext = rootAstContext;
    }
    
    public static List<LogicInstruction> resolve(CompilerProfile profile, InstructionProcessor processor,
            AstContext rootAstContext, List<LogicInstruction> program) {
        return new LogicInstructionLabelResolver(profile, processor, rootAstContext).resolve(program);
    }

    private List<LogicInstruction> resolve(List<LogicInstruction> program) {
        return sortVariables(resolveLabels(program, List.of()));
    }

    public List<LogicInstruction> sortVariables(List<LogicInstruction> program) {
        if (profile.getSortVariables().isEmpty() || program.isEmpty()) {
            return program;
        }

        int limit = profile.getInstructionLimit() - InstructionCounter.localSize(program) - 2;
        if (limit <= 0) {
            return program;
        }

        HashSet<LogicVariable> allVariables = program.stream()
                .flatMap(LogicInstruction::inputOutputArgumentsStream)
                .filter(a -> a.isUserVariable() || a.getType() == ArgumentType.BLOCK)
                .map(LogicVariable.class::cast)
                .collect(Collectors.toCollection(HashSet::new));

        List<LogicVariable> order = orderVariables(allVariables, profile.getSortVariables());

        AstContext astContext = MindcodeCompiler.getContext().getRootAstContext()
                .createSubcontext(AstContextType.JUMPS, AstSubcontextType.BASIC, 1.0);
        List<LogicInstruction> variables = createVariables(astContext, order);

        int instructions = Math.min(limit, variables.size());

        List<LogicInstruction> result = new ArrayList<>();

        if (program.getFirst().getAstContext().matches(AstContextType.JUMPS, AstSubcontextType.REMOTE_INIT)) {
            if (program.getFirst() instanceof JumpInstruction jump && jump.isUnconditional()) {
                int index = 0;
                while (program.get(index).getOpcode() == JUMP) index++;
                if (program.get(index) instanceof LabelInstruction label && jump.getTarget().equals(label.getLabel())) {
                    result.addAll(program.subList(0, index));
                    result.addAll(variables);
                    result.addAll(program.subList(index, program.size()));
                    return result;
                }
            }

            throw new MindcodeInternalError("Unexpected program structure.");
        } else {
            LogicLabel logicLabel = processor.nextLabel();
            result.add(processor.createJumpUnconditional(astContext, logicLabel));
            result.addAll(variables.subList(0, instructions));
            result.add(processor.createLabel(astContext, logicLabel));
            result.addAll(program);
        }

        return result;
    }

    public static List<LogicVariable> orderVariables(Set<LogicVariable> allVariables, List<SortCategory> categories) {
        // Sort all categories except ALL
        Map<SortCategory, List<LogicVariable>> sorted = new EnumMap<>(SortCategory.class);
        for (SortCategory category : categories) {
            if (category != SortCategory.ALL) {
                List<LogicVariable> selected = allVariables.stream()
                        .filter(v -> matches(v, category))
                        .sorted(Comparator.comparing(LogicArgument::toMlog))
                        .toList();

                sorted.put(category, selected);
                selected.forEach(allVariables::remove);
            }
        }

        // What remains is ALL
        sorted.put(SortCategory.ALL, allVariables.stream().sorted(Comparator.comparing(LogicVariable::getFullName)).toList());

        // Now put categories in the proper order
        List<LogicVariable> order = new ArrayList<>();
        for (SortCategory category : categories) {
            List<LogicVariable> variables = sorted.remove(category);
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

    public List<LogicInstruction> resolveLabels(List<LogicInstruction> program, List<LogicVariable> initVariables) {
        AstContext astContext = MindcodeCompiler.getContext().getRootAstContext()
                .createSubcontext(AstContextType.CREATE_VARS, AstSubcontextType.BASIC, 1.0);

        if (program.isEmpty()) {
            return createVariables(astContext, initVariables);
        }

        // Save the last instruction before it is resolved
        LogicInstruction last = program.getLast();

        program = resolveRemarks(program);
        calculateAddresses(program);
        program = resolveAddresses(resolveVirtualInstructions(program));

        boolean addSignature;
        if (!initVariables.isEmpty()) {
            if (!last.endsCodePath()) {
                program.add(processor.createEnd(last.getAstContext()));
            }

            Set<LogicVariable> missingVariables = new LinkedHashSet<>(initVariables);
            program.stream()
                    .mapMulti((LogicInstruction instruction, Consumer<LogicVariable> consumer)
                            -> instruction.inputOutputArgumentsStream()
                            .filter(LogicVariable.class::isInstance)
                            .map(LogicVariable.class::cast)
                            .forEach(consumer))
                    .forEach(missingVariables::remove);

            program.addAll(createVariables(astContext, missingVariables));
            addSignature = true;
        } else {
            addSignature = last.endsCodePath();
        }

        if (addSignature && profile.isSignature() && program.size() < profile.getInstructionLimit()) {
            program.add(processor.createPrint(last.getAstContext(), LogicString.create(CompilerProfile.SIGNATURE)));
        }

        return program;
    }

    private List<LogicInstruction> resolveRemarks(List<LogicInstruction> program) {
        return switch (profile.getRemarks()) {
            case ACTIVE     -> resolveRemarksActive(program);
            case COMMENTS   -> resolveRemarksComment(program);
            case NONE       -> resolveRemarksNone(program);
            case PASSIVE    -> resolveRemarksPassive(program);
        };
    }

    private List<LogicInstruction> resolveRemarksActive(List<LogicInstruction> program) {
        return program.stream()
                .map(ix -> ix instanceof RemarkInstruction r ? processor.createPrint(r.getAstContext(), r.getValue()) : ix)
                .toList();
    }

    private List<LogicInstruction> resolveRemarksComment(List<LogicInstruction> program) {
        return program.stream()
                .map(ix -> ix instanceof RemarkInstruction r ? processor.createComment(r.getAstContext(), r.getValue()) : ix)
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

    private static final Set<Integer> invalidAddresses = Set.of(0, (int) '\r',  (int) '"', (int) '\\');

    private void calculateAddresses(List<LogicInstruction> program) {
        boolean alignMultiLabes = profile.useTextJumpTables();
        int instructionPointer = 0;
        for (int i = 0; i < program.size(); i++) {
            final LogicInstruction instruction = program.get(i);
            if (alignMultiLabes && instruction.isJumpTarget() && invalidAddresses.contains(instructionPointer)) {
                program.add(i++, processor.createInstruction(instruction.getAstContext(), Opcode.NOOP));
                instructionPointer++;
            }

            instructionPointer += instruction.getRealSize(null);
            if (instruction instanceof LabeledInstruction ix) {
                if (addresses.containsKey(ix.getLabel())) {
                    throw new MindcodeInternalError("Duplicate label detected: '%s' reused at least twice in %s", ix.getLabel(), program);
                }

                addresses.put(ix.getLabel(), LogicLabel.absolute(instructionPointer));
                labelInstructions.put(ix.getLabel(), instruction);

                addressContexts.compute(instructionPointer, (k, v) -> v == null ? ix.getAstContext()
                        : v.level() < ix.getAstContext().level() ? v : ix.getAstContext());
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
        int comments = 0;
        boolean wrongAddress = false;

        for (final LogicInstruction instruction : program) {
            if (instruction instanceof CommentInstruction) comments++;

            if (instruction instanceof MultiCallInstruction ix) {
                if (resolveLabel(ix.getTarget()) instanceof LogicLabel label && label.getAddress() >= 0) {
                    LogicInstruction newInstruction = processor.createInstruction(ix.getAstContext(),
                            OP, Operation.ADD, LogicBuiltIn.COUNTER, LogicNumber.create(label.getAddress()), ix.getOffset());
                    result.add(newInstruction);
                } else {
                    throw new MindcodeInternalError("MultiCall target '%s' is not a label.", ix.getTarget());
                }
            } else if (instruction instanceof MultiJumpInstruction ix) {
                List<LogicLabel> jumpTable = ix.getJumpTable();
                if (!jumpTable.isEmpty()) {
                    // Build the string jump table
                    StringBuilder sbr = new StringBuilder(jumpTable.size() + 1);
                    int index = 0;
                    for (LogicLabel label : jumpTable) {
                        int address = ((LogicLabel) resolveLabel(label)).getAddress();
                        sbr.append(address == 10 ? "\\n" : (char) address);
                    }
                    LogicString jumpTableString = LogicString.create(ix.sourcePosition(), sbr.toString());
                    LogicInstruction newInstruction = processor.createInstruction(ix.getAstContext(),
                            READ, LogicBuiltIn.COUNTER, jumpTableString, ix.getTarget());
                    result.add(newInstruction);
                } else if (ix.getTarget() instanceof LogicVariable var) {
                    LogicInstruction newInstruction = processor.createInstruction(ix.getAstContext(),
                            SET, LogicBuiltIn.COUNTER, var);
                    result.add(newInstruction);
                } else if (resolveLabel(ix.getTarget()) instanceof LogicLabel label && label.getAddress() >= 0) {
                    if (profile.isSymbolicLabels()) {
                        int offset = ix.getOffset().getIntValue();
                        LogicArgument counterOffset;
                        if (offset != 0) {
                            counterOffset = processor.nextTemp();
                            result.add(processor.createInstruction(ix.getAstContext(),
                                    OP, Operation.SUB, counterOffset, ix.getValue(), ix.getOffset()));
                        } else {
                            counterOffset = ix.getValue();
                        }
                        result.add(processor.createInstruction(ix.getAstContext(),
                                OP, Operation.ADD, LogicBuiltIn.COUNTER, LogicBuiltIn.COUNTER, counterOffset));
                        int currentIndex = result.size() - comments;

                        if (labelInstructions.entrySet().stream()
                                .filter(e -> e.getValue().getMarker().equals(ix.getMarker()))
                                //.peek(e -> System.out.println("    " + e.getKey().toMlog() + " -> " + e.getValue() + " @ " + addresses.get(e.getKey()).getAddress()))
                                .noneMatch(e -> addresses.get(e.getKey()).getAddress() == currentIndex)) {
                            //System.out.println("Missing label at index " + currentIndex);
                            wrongAddress = true;
                        } else {
                            //System.out.println("Label found at index " + currentIndex);
                        }
                    } else {
                        int offset = label.getAddress() - ix.getOffset().getIntValue();
                        result.add(processor.createInstruction(ix.getAstContext(),
                                OP, Operation.ADD, LogicBuiltIn.COUNTER, ix.getValue(), LogicNumber.create(offset)));
                    }
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

        if (profile.isSymbolicLabels() && wrongAddress) {
            processor.error(ERR.LABEL_ADDRESS_MISMATCH);
        }

        return result;
    }

    private List<LogicInstruction> resolveVirtualInstructions(List<LogicInstruction> program) {
        return program.stream().mapMulti((LogicInstruction instruction, Consumer<LogicInstruction> consumer)
                -> processor.resolve(profile, instruction, consumer)).toList();
    }

    private List<LogicInstruction> createVariables(AstContext astContext, Collection<LogicVariable> variables) {
        final int batchSize = 6;

        List<LogicInstruction> result = new ArrayList<>();
        List<LogicArgument> vars = new ArrayList<>(variables);

        while (vars.size() % batchSize > 0) {
            vars.add(LogicNumber.ZERO);
        }

        vars.addFirst(LogicKeyword.create("triangle"));
        while (vars.size() > 1) {
            result.add(processor.createInstruction(astContext, DRAW, vars.subList(0, batchSize + 1)));
            vars.subList(1, batchSize + 1).clear();
        }

        return result;
    }

    public List<LogicInstruction> generateSymbolicLabels(List<LogicInstruction> instructions) {
        if (profile.isSymbolicLabels()) {
            final String labelPrefix = "label_";

            BitSet labels = new BitSet(instructions.size());
            for (LogicInstruction instruction : instructions) {
                if (instruction instanceof JumpInstruction jump) {
                    labels.set(jump.getTarget().getAddress());
                }
            }

            List<LogicInstruction> result = new ArrayList<>();
            result.add(processor.createComment(rootAstContext, "Mlog code compiled with support for symbolic labels"));
            result.add(processor.createComment(rootAstContext, "You can safely add/remove instructions, in most parts of the program"));
            result.add(processor.createComment(rootAstContext, "Pay closer attention to sections of the program manipulating @counter"));

            int counter = 0;
            for (LogicInstruction instruction : instructions) {
                if (instruction.isReal()) {
                    if (labels.get(counter)) {
                        result.add(processor.createLabel(addressContexts.getOrDefault(counter, rootAstContext),
                                LogicLabel.symbolic(labelPrefix + counter)));
                    }

                    counter++;
                    if (instruction instanceof JumpInstruction jump) {
                        result.add(jump.withTarget(LogicLabel.symbolic(labelPrefix + jump.getTarget().getAddress())));
                        continue;
                    }
                }
                result.add(instruction);
            }

            return result;
        } else {
            return instructions;
        }
    }

}
