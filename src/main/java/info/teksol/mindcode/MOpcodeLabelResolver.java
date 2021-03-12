package info.teksol.mindcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MOpcodeLabelResolver {
    public static List<MOpcode> resolve(List<MOpcode> program) {
        final Map<String, Integer> addresses = calculateAddresses(program);
        return resolveAddresses(program, addresses);
    }

    private static List<MOpcode> resolveAddresses(List<MOpcode> program, Map<String, Integer> addresses) {
        final List<MOpcode> result = new ArrayList<>();
        for (final MOpcode node : program) {
            if (node.getOpcode().equals("label")) continue;
            if (!node.getOpcode().equals("jump")) {
                result.add(node);
                continue;
            }

            final String label = node.getArgs().get(0);
            if (!addresses.containsKey(label)) {
                throw new MindustryConverterException("Unknown jump label target: [" + label + "] was not previously discovered in " + program);
            }

            final List<String> newArgs = new ArrayList<>(node.getArgs().subList(1, node.getArgs().size()));
            newArgs.add(0, addresses.get(label).toString());
            result.add(new MOpcode("jump", newArgs));
        }

        return result;
    }

    private static Map<String, Integer> calculateAddresses(List<MOpcode> program) {
        final Map<String, Integer> result = new HashMap<>();
        int instructionPointer = 0;
        for (int i = 0; i < program.size(); i++) {
            final MOpcode node = program.get(i);
            if (!node.getOpcode().equals("label")) {
                instructionPointer++;
                continue;
            }

            final String label = node.getArgs().get(0);
            if (result.containsKey(label)) {
                throw new MindustryConverterException("Duplicate label detected: [" + label + "] reused at least twice in " + program);
            }

            result.put(label, instructionPointer);
        }

        return result;
    }
}
