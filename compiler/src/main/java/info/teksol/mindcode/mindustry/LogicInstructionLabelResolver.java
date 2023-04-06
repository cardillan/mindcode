package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;

public class LogicInstructionLabelResolver {
    private final InstructionProcessor instructionProcessor;

    public LogicInstructionLabelResolver(InstructionProcessor instructionProcessor) {
        this.instructionProcessor = instructionProcessor;
    }
    
    public static List<LogicInstruction> resolve(InstructionProcessor instructionProcessor, List<LogicInstruction> program) {
        return new LogicInstructionLabelResolver(instructionProcessor).resolve(program);
    }

    private List<LogicInstruction> resolve(List<LogicInstruction> program) {
        final Map<String, String> addresses = calculateAddresses(program);
        return resolveAddresses(resolveVirtualInstructions(program), addresses);
    }

    private LogicInstruction replaceArg(LogicInstruction instruction, int argIndex, String value) {
        return instructionProcessor.replaceArg(instruction, argIndex, value);
    }

    // If there's a known label at given argument index, resolves it, otherwise returns the original instruction
    private LogicInstruction resolveLabel(Map<String, String> addresses, LogicInstruction instruction, int labelIndex) {
        final String arg = instruction.getArg(labelIndex);
        return replaceArg(instruction, labelIndex, addresses.getOrDefault(arg, arg));
    }

    private List<LogicInstruction> resolveAddresses(List<LogicInstruction> program, Map<String, String> addresses) {
        final List<LogicInstruction> result = new ArrayList<>();
        for (final LogicInstruction instruction : program) {
            switch (instruction.getOpcode()) {
                case JUMP:
                    final String label = instruction.getArg(0);
                    if (!addresses.containsKey(label)) {
                        throw new CompilerException("Unknown jump label target: [" + label + "] was not previously discovered in " + program);
                    }

                    result.add(resolveLabel(addresses, instruction, 0));
                    break;
                
                case SET:
                    result.add(resolveLabel(addresses, instruction, 1));
                    break;

                case WRITE: 
                    result.add(resolveLabel(addresses, instruction, 0));
                    break;

                default:
                    result.add(instruction);
                    break;
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

    private Map<String, String> calculateAddresses(List<LogicInstruction> program) {
        final Map<String, String> result = new HashMap<>();
        int instructionPointer = 0;
        for (int i = 0; i < program.size(); i++) {
            final LogicInstruction instruction = program.get(i);
            instructionPointer += instructionProcessor.getRealSize(instruction);
            if (instruction.isLabel()) {
                final String label = instruction.getArg(0);
                if (result.containsKey(label)) {
                    throw new CompilerException("Duplicate label detected: [" + label + "] reused at least twice in " + program);
                }

                result.put(label, String.valueOf(instructionPointer));
            }
        }

        return result;
    }
}
