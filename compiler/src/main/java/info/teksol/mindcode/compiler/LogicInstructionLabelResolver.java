package info.teksol.mindcode.compiler;

import info.teksol.mindcode.compiler.instructions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        final String label = instruction.getArg(labelIndex);
        return replaceArg(instruction, labelIndex, addresses.getOrDefault(label, label));
    }

    private List<LogicInstruction> resolveAddresses(List<LogicInstruction> program, Map<String, String> addresses) {
        final List<LogicInstruction> result = new ArrayList<>();
        for (final LogicInstruction instruction : program) {
            switch (instruction) {
                case JumpInstruction ix:
                    final String label = ix.getTarget();
                    if (!addresses.containsKey(label)) {
                        throw new CompilerException("Unknown jump label target: [" + label + "] was not previously discovered in " + program);
                    }
                    result.add(resolveLabel(addresses, ix, 0));
                    break;
                
                case SetInstruction ix:
                    result.add(resolveLabel(addresses, ix, 1));
                    break;

                case WriteInstruction ix:
                    result.add(resolveLabel(addresses, ix, 0));
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
            if (instruction instanceof LabelInstruction ix) {
                if (result.containsKey(ix.getLabel())) {
                    throw new CompilerException("Duplicate label detected: [" + ix.getLabel() + "] reused at least twice in " + program);
                }

                result.put(ix.getLabel(), String.valueOf(instructionPointer));
            }
        }

        return result;
    }
}
