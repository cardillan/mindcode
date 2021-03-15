package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.GenerationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogicInstructionLabelResolver {
    public static List<LogicInstruction> resolve(List<LogicInstruction> program) {
        final Map<String, Integer> addresses = calculateAddresses(program);
        return resolveAddresses(program, addresses);
    }

    private static List<LogicInstruction> resolveAddresses(List<LogicInstruction> program, Map<String, Integer> addresses) {
        final List<LogicInstruction> result = new ArrayList<>();
        for (final LogicInstruction instruction : program) {
            if (instruction.getOpcode().equals("label")) continue;
            if (!instruction.getOpcode().equals("jump")) {
                result.add(instruction);
                continue;
            }

            final String label = instruction.getArgs().get(0);
            if (!addresses.containsKey(label)) {
                throw new GenerationException("Unknown jump label target: [" + label + "] was not previously discovered in " + program);
            }

            final List<String> newArgs = new ArrayList<>(instruction.getArgs().subList(1, instruction.getArgs().size()));
            newArgs.add(0, addresses.get(label).toString());
            result.add(new LogicInstruction("jump", newArgs));
        }

        return result;
    }

    private static Map<String, Integer> calculateAddresses(List<LogicInstruction> program) {
        final Map<String, Integer> result = new HashMap<>();
        int instructionPointer = 0;
        for (int i = 0; i < program.size(); i++) {
            final LogicInstruction instruction = program.get(i);
            if (!instruction.getOpcode().equals("label")) {
                instructionPointer++;
                continue;
            }

            final String label = instruction.getArgs().get(0);
            if (result.containsKey(label)) {
                throw new GenerationException("Duplicate label detected: [" + label + "] reused at least twice in " + program);
            }

            result.put(label, instructionPointer);
        }

        return result;
    }
}
