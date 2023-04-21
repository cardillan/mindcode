package info.teksol.mindcode.compiler;

import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.logic.ArgumentType;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicLabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogicInstructionLabelResolver {
    private final InstructionProcessor instructionProcessor;

    private final Map<LogicLabel, LogicLabel> addresses = new HashMap<>();

    public LogicInstructionLabelResolver(InstructionProcessor instructionProcessor) {
        this.instructionProcessor = instructionProcessor;
    }
    
    public static List<LogicInstruction> resolve(InstructionProcessor instructionProcessor, List<LogicInstruction> program) {
        return new LogicInstructionLabelResolver(instructionProcessor).resolve(program);
    }

    private List<LogicInstruction> resolve(List<LogicInstruction> program) {
        calculateAddresses(program);
        return resolveAddresses(resolveVirtualInstructions(program), addresses);
    }


    private void calculateAddresses(List<LogicInstruction> program) {
        int instructionPointer = 0;
        for (int i = 0; i < program.size(); i++) {
            final LogicInstruction instruction = program.get(i);
            instructionPointer += instruction.getRealSize();
            if (instruction instanceof LabelInstruction ix) {
                if (addresses.containsKey(ix.getLabel())) {
                    throw new CompilerException("Duplicate label detected: [" + ix.getLabel() + "] reused at least twice in " + program);
                }

                addresses.put(ix.getLabel(), LogicLabel.absolute(instructionPointer));
            }
        }
    }

    private LogicArgument resolveLabel(LogicArgument argument) {
        if (argument instanceof LogicLabel label) {
            if (!addresses.containsKey(label)) {
                throw new CompilerException("Unknown jump label target: '" + label + "' was not previously discovered in program");
            }
            return addresses.get(label);
        } else {
            return argument;
        }
    }

    private List<LogicInstruction> resolveAddresses(List<LogicInstruction> program, Map<LogicLabel, LogicLabel> addresses) {
        final List<LogicInstruction> result = new ArrayList<>();

        for (final LogicInstruction instruction : program) {
            if (instruction.getArgs().stream().anyMatch(a -> a.getType() == ArgumentType.LABEL)) {
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
        final List<LogicInstruction> result = new ArrayList<>();
        for (final LogicInstruction instruction : program) {
            if (instruction.getOpcode().isVirtual()) {
                result.addAll(instructionProcessor.resolve(instruction));
            } else {
                result.add(instruction);
            }
        }
        return result;
    }
}
