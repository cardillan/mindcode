package info.teksol.mc.mindcode.compiler.postprocess;

import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.InstructionCounter;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationCoordinator;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GlobalCompilerProfile;
import info.teksol.mc.profile.SortCategory;
import info.teksol.mc.util.CollectionUtils;
import info.teksol.mc.util.StringUtils;
import info.teksol.mc.util.Utf8Utils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
public class LogicInstructionLabelResolver {
    private final GlobalCompilerProfile profile;
    private final InstructionProcessor processor;
    private final AstContext rootAstContext;

    private final Map<LogicLabel, LogicInstruction> labelInstructions = new HashMap<>();
    private final Map<LogicLabel, LogicLabel> addresses = new HashMap<>();
    private final Map<Integer, AstContext> addressContexts = new HashMap<>();
    private final Map<Integer, Set<LogicInstruction>> textJumpOrigins = new HashMap<>();
    private final Map<Integer, Set<Integer>> textJumpKeys = new HashMap<>();

    public LogicInstructionLabelResolver(GlobalCompilerProfile profile, InstructionProcessor processor, AstContext rootAstContext) {
        this.processor = processor;
        this.profile = profile;
        this.rootAstContext = rootAstContext;
    }
    
    public static List<LogicInstruction> resolve(GlobalCompilerProfile profile, InstructionProcessor processor,
            AstContext rootAstContext, List<LogicInstruction> program) {
        return new LogicInstructionLabelResolver(profile, processor, rootAstContext).resolve(program);
    }

    private List<LogicInstruction> resolve(List<LogicInstruction> program) {
        return sortVariables(resolveLabels(program, Set.of()));
    }

    public List<LogicInstruction> sortVariables(List<LogicInstruction> program) {
        List<SortCategory> categories = profile.getSortVariables();
        if (categories.isEmpty() || categories.equals(List.of(SortCategory.NONE)) || program.isEmpty()) {
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

        List<LogicVariable> order = orderVariables(allVariables, categories);

        AstContext astContext = MindcodeCompiler.getContext().getRootAstContext()
                .createSubcontext(AstContextType.CREATE_VARS, AstSubcontextType.BASIC, 1.0);
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

    public List<LogicInstruction> resolveLabels(List<LogicInstruction> program, Set<LogicVariable> forcedVariables) {
        AstContext astContext = MindcodeCompiler.getContext().getRootAstContext()
                .createSubcontext(AstContextType.CREATE_VARS, AstSubcontextType.BASIC, 1.0);

        if (program.isEmpty()) {
            return createVariables(astContext, forcedVariables);
        }

        // Save the last instruction before it is resolved
        LogicInstruction last = program.getLast();

        program = resolveRemarks(program);
        calculateAddresses(program);
        program = resolveAddresses(resolveVirtualInstructions(program));

        addTargetComments(program);

        Set<LogicVariable> missingVariables = new LinkedHashSet<>(forcedVariables);
        program.forEach(ix -> missingVariables.addAll(ix.getIndirectVariables()));

        boolean addSignature = last.endsCodePath();
        if (!missingVariables.isEmpty()) {
            program.forEach(ix -> ix.getTypedArguments().forEach(a -> {
                if (a.argument() instanceof LogicVariable variable) {
                    missingVariables.remove(variable);
                }
            }));

            if (!missingVariables.isEmpty()) {
                if (!last.endsCodePath()) {
                    program.add(processor.createEnd(last.getAstContext()));
                }
                program.addAll(createVariables(astContext, missingVariables));
                addSignature = true;
            }
        }

        if (addSignature && profile.isSignature() && program.size() < profile.getInstructionLimit()) {
            program.add(processor.createPrint(last.getAstContext(), LogicString.create(CompilerProfile.SIGNATURE)));
        }

        return program;
    }

    private List<LogicInstruction> resolveRemarks(List<LogicInstruction> program) {
        return new RemarksResolver().resolveRemarks(program);
    }

    private void calculateAddresses(List<LogicInstruction> program) {
        int instructionPointer = 0;
        for (int i = 0; i < program.size(); i++) {
            final LogicInstruction instruction = program.get(i);
            if (instruction.isJumpTarget() && Utf8Utils.INVALID_CHARS.contains(instructionPointer)) {
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

                addressContexts.compute(instructionPointer, (_, v) -> v == null ? ix.getAstContext()
                        : v.level() < ix.getAstContext().level() ? v : ix.getAstContext());
            }
        }
    }

    private LogicArgument resolveLabel(LogicArgument argument) {
        if (argument instanceof LogicLabel label) {
            if (!addresses.containsKey(label)) {
                if (OptimizationCoordinator.IGNORE_UNKNOWN_LABELS) {
                    System.out.println("Unknown label " + label);
                    return LogicLabel.absolute(10000);
                } else {
                    throw new MindcodeInternalError("Unknown jump label target: '%s' was not previously discovered in program.", label);
                }
            }
            return addresses.get(label);
        } else {
            return argument;
        }
    }

    private int resolveAddress(LogicLabel label) {
        return ((LogicLabel) resolveLabel(label)).getAddress();
    }

    private List<LogicInstruction> resolveAddresses(List<LogicInstruction> program) {
        final List<LogicInstruction> result = new ArrayList<>();
        int comments = 0;
        boolean wrongAddress = false;

        for (final LogicInstruction instruction : program) {
            if (instruction instanceof CommentInstruction) comments++;

            List<LogicLabel> jumpTable = instruction.getJumpTable();
            if (!jumpTable.isEmpty()) {
                result.add(buildTextTableJump((MultiTargetInstruction) instruction, jumpTable));
            } else if (instruction instanceof MultiCallInstruction ix) {
                if (resolveLabel(ix.getTarget()) instanceof LogicLabel label && label.getAddress() >= 0) {
                    LogicInstruction newInstruction = processor.createInstruction(ix.getAstContext(),
                            OP, Operation.ADD, LogicBuiltIn.COUNTER, LogicNumber.create(label.getAddress()), ix.getOffset());
                    result.add(newInstruction);
                } else {
                    throw new MindcodeInternalError("MultiCall target '%s' is not a label.", ix.getTarget());
                }
            } else if (instruction instanceof MultiJumpInstruction ix) {
                if (ix.getTarget() instanceof LogicVariable var) {
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

    private void addTargetComments(List<LogicInstruction> program) {
        for (Map.Entry<Integer, Set<Integer>> entry : textJumpKeys.entrySet()) {
            int address = entry.getKey();
            if (address >= 0 && address < program.size()) {
                List<String> origins = textJumpOrigins.get(address).stream()
                        .map(ix -> CollectionUtils.indexOf(program, px -> ix == px))
                        .sorted().limit(3).map(String::valueOf)
                        .collect(Collectors.toCollection(ArrayList::new));

                if (origins.size() < textJumpOrigins.get(address).size()) {
                    origins.add("...");
                }

                program.get(address).setComment("# Origin: " + String.join(", ", origins)
                        + ", keys: " + StringUtils.joinCompressedRanges(entry.getValue()));
            }
        }
    }

    private LogicInstruction buildTextTableJump(MultiTargetInstruction ix, List<LogicLabel> jumpTable) {
        int[] addresses = jumpTable.stream().mapToInt(this::resolveAddress).toArray();
        LogicString jumpTableString = LogicString.create(ix.sourcePosition(), Utf8Utils.encode(addresses));

        LogicInstruction instruction = processor.createInstruction(ix.getAstContext(), READ, LogicBuiltIn.COUNTER,
                jumpTableString, ix.getTarget());

        for (int i = 0; i < addresses.length; i++) {
            textJumpOrigins.computeIfAbsent(addresses[i], _ -> Collections.newSetFromMap(new IdentityHashMap<>())).add(instruction);
            textJumpKeys.computeIfAbsent(addresses[i], _ -> new HashSet<>()).add(i);
        }

        return instruction;
    }

    private List<LogicInstruction> resolveVirtualInstructions(List<LogicInstruction> program) {
        return program.stream().mapMulti(processor::resolve).toList();
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

    private class RemarksResolver {
        private final List<LogicInstruction> result = new ArrayList<>();
        private @Nullable LabelInstruction activeLabel;

        private List<LogicInstruction> resolveRemarks(List<LogicInstruction> program) {
            for (LogicInstruction ix : program) {
                if (ix instanceof RemarkInstruction r) {
                    switch (r.getAstContext().getLocalProfile().getRemarks()) {
                        case NONE       -> { /* do nothing */ }
                        case COMMENTS   -> add(processor.createComment(r.getAstContext(), r.getValue()), false);
                        case PASSIVE    -> add(processor.createPrint(r.getAstContext(), r.getValue()),false);
                        case ACTIVE     -> add(processor.createPrint(r.getAstContext(), r.getValue()),true);
                    }
                } else {
                    add(ix, true);
                }
            }

            if (activeLabel != null) {
                result.add(activeLabel);
                result.add(processor.createEnd(activeLabel.getAstContext()));
            }

            return result;
        }

        private void add(LogicInstruction instruction, boolean active) {
            if (!(instruction instanceof CommentInstruction)) {
                if (active) {
                    if (activeLabel != null) {
                        result.add(activeLabel);
                        activeLabel = null;
                    }
                } else if (activeLabel == null) {
                    LogicLabel label = processor.nextLabel();
                    activeLabel = processor.createLabel(instruction.getAstContext(), label);
                    result.add(processor.createJumpUnconditional(instruction.getAstContext(), label));
                }
            }

            result.add(instruction);
        }
    }
}
