package info.teksol.mindcode.compiler;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.logic.*;

import java.util.*;
import java.util.stream.Collectors;

import static info.teksol.mindcode.logic.Opcode.OP;
import static info.teksol.mindcode.logic.Opcode.PACKCOLOR;

// TODO Rename class
public class LogicInstructionLabelResolver {
    private final CompilerProfile profile;
    private final InstructionProcessor instructionProcessor;

    private final Map<LogicLabel, LogicLabel> addresses = new HashMap<>();

    public LogicInstructionLabelResolver(InstructionProcessor instructionProcessor, CompilerProfile profile) {
        this.instructionProcessor = instructionProcessor;
        this.profile = profile;
    }
    
    public static List<LogicInstruction> resolve(InstructionProcessor instructionProcessor, CompilerProfile profile,
            List<LogicInstruction> program) {
        return new LogicInstructionLabelResolver(instructionProcessor, profile).resolve(program);
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

        int limit = profile.getInstructionLimit() - program.stream().mapToInt(LogicInstruction::getRealSize).sum();
        if (limit <= 0) {
            return program;
        }

        int instructions = Math.min(limit, order.size() / 4);

        List<LogicInstruction> result = new ArrayList<>();
        AstContext astContext = program.get(0).getAstContext();
        for (int index = 0, i = 0; i++ < instructions; index += 5) {
            order.add(index, LogicNull.NULL);
            result.add(instructionProcessor.createInstruction(astContext,  PACKCOLOR, order.subList(index, index + 5)));
        }

        result.addAll(program);
        return result;
    }

    static List<LogicArgument> orderVariables(Set<LogicArgument> allVariables, List<SortCategory> categories) {
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
            case PARAMS -> logicVariable.getType() == ArgumentType.PARAMETER;
            case GLOBALS    -> logicVariable.getType() == ArgumentType.GLOBAL_VARIABLE;
            case MAIN       -> logicVariable.isMainVariable();
            case LOCALS     -> logicVariable.isFunctionVariable();
            case NONE       -> false;
            case ALL        -> true;
        };
    }

    public List<LogicInstruction> resolveLabels(List<LogicInstruction> program) {
        program = resolveRemarks(program);
        calculateAddresses(program);
        return resolveAddresses(resolveVirtualInstructions(program));
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
                .map(ix -> ix instanceof RemarkInstruction r ? instructionProcessor.createPrint(r.getAstContext(), r.getValue()) : ix)
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
                    LogicLabel label = instructionProcessor.nextLabel();
                    activeLabel = instructionProcessor.createLabel(ix.getAstContext(),label);
                    result.add(instructionProcessor.createJumpUnconditional(ix.getAstContext(), label));
                }
                result.add(instructionProcessor.createPrint(ix.getAstContext(), ((RemarkInstruction) ix).getValue()));
            } else {
                if (activeLabel != null) {
                    result.add(activeLabel);
                    activeLabel = null;
                }
                result.add(ix);
            }
        }

        return result;
    }

    private void calculateAddresses(List<LogicInstruction> program) {
        int instructionPointer = 0;
        for (int i = 0; i < program.size(); i++) {
            final LogicInstruction instruction = program.get(i);
            instructionPointer += instruction.getRealSize();
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
            if (instruction instanceof GotoOffsetInstruction ix) {
                if (resolveLabel(ix.getTarget()) instanceof LogicLabel label && label.getAddress() >= 0) {
                    int offset = label.getAddress() - ix.getOffset().getIntValue();
                    LogicInstruction newInstruction = instructionProcessor.createInstruction(ix.getAstContext(),
                            OP, Operation.ADD, LogicBuiltIn.COUNTER, ix.getValue(), LogicNumber.get(offset));
                    result.add(newInstruction);
                } else {
                    throw new MindcodeInternalError("GotoOffset target '%s' is not a label.", ix.getTarget());
                }
            } else if (instruction.getArgs().stream().anyMatch(a -> a.getType() == ArgumentType.LABEL)) {
                List<LogicArgument> newArgs = instruction.getArgs().stream().map(this::resolveLabel).toList();
                LogicInstruction newInstruction = instructionProcessor.replaceArgs(instruction, newArgs);
                result.add(newInstruction);
            } else {
                result.add(instruction);
            }
        }
        return result;
    }

    private List<LogicInstruction> resolveVirtualInstructions(List<LogicInstruction> program) {
        return program.stream().mapMulti(instructionProcessor::resolve).toList();
    }
}
