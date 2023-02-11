package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.logic.Opcode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;

import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;

public class LogicInstructionLabelResolver {
    private final InstructionProcessor instructionProcessor;

    public LogicInstructionLabelResolver(InstructionProcessor instructionProcessor) {
        this.instructionProcessor = instructionProcessor;
    }
    
    public static List<LogicInstruction> resolve(InstructionProcessor instructionProcessor, List<LogicInstruction> program) {
        return new LogicInstructionLabelResolver(instructionProcessor).resolve(program);
    }

    private LogicInstruction createInstruction(Opcode opcode, String... args) {
        return instructionProcessor.createInstruction(opcode, args);
    }

    private LogicInstruction createInstruction(Opcode opcode, List<String> args) {
        return instructionProcessor.createInstruction(opcode, args);
    }

    private List<LogicInstruction> resolve(List<LogicInstruction> program) {
        final Map<String, Integer> addresses = calculateAddresses(program);
        return resolveAddresses(program, addresses);
    }

    private List<LogicInstruction> resolveAddresses(List<LogicInstruction> program, Map<String, Integer> addresses) {
        final List<LogicInstruction> result = new ArrayList<>();
        for (final LogicInstruction instruction : program) {
            if (instruction.isLabel()) continue;
            switch (instruction.getOpcode()) {
                case JUMP:
                    final String label = instruction.getArgs().get(0);
                    if (!addresses.containsKey(label)) {
                        throw new CompilerException("Unknown jump label target: [" + label + "] was not previously discovered in " + program);
                    }

                    resolveJump(label, instruction, addresses, result);
                    break;

                case SET:
                    if (addresses.containsKey(instruction.getArgs().get(1))) {
                        result.add(createInstruction(Opcode.SET, instruction.getArgs().get(0), addresses.get(instruction.getArgs().get(1)).toString()));
                    } else {
                        result.add(instruction);
                    }
                    break;



                case WRITE:
                    if (addresses.containsKey(instruction.getArgs().get(0))) {
                        result.add(createInstruction(Opcode.WRITE, addresses.get(instruction.getArgs().get(0)).toString(), instruction.getArgs().get(1), instruction.getArgs().get(2)));
                    } else {
                        result.add(instruction);
                    }
                    break;



                default:
                    result.add(instruction);
                    break;
            }
        }

        return result;
    }

    private void resolveJump(String label, LogicInstruction instruction, Map<String, Integer> addresses, List<LogicInstruction> result) {
        final List<String> newArgs = new ArrayList<>(instruction.getArgs().subList(1, instruction.getArgs().size()));
        newArgs.add(0, addresses.get(label).toString());
        result.add(createInstruction(Opcode.JUMP, newArgs));
    }

    private Map<String, Integer> calculateAddresses(List<LogicInstruction> program) {
        final Map<String, Integer> result = new HashMap<>();
        int instructionPointer = 0;
        for (int i = 0; i < program.size(); i++) {
            final LogicInstruction instruction = program.get(i);
            if (!instruction.isLabel()) {
                instructionPointer++;
                continue;
            }

            final String label = instruction.getArgs().get(0);
            if (result.containsKey(label)) {
                throw new CompilerException("Duplicate label detected: [" + label + "] reused at least twice in " + program);
            }

            result.put(label, instructionPointer);
        }

        return result;
    }
}
