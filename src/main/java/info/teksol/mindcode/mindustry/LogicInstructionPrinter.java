package info.teksol.mindcode.mindustry;

import edu.emory.mathcs.backport.java.util.Collections;
import info.teksol.mindcode.GenerationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogicInstructionPrinter {
    private static final Map<String, Integer> FN_ARGS;

    static {
        Map<String, Integer> args = new HashMap<>();
        args.put("set", 2);
        args.put("op", 4);
        args.put("jump", 4);
        args.put("print", 1);
        args.put("end", 0);
        args.put("sensor", 3);
        args.put("control", 6);
        args.put("write", 3);
        args.put("read", 3);
        args.put("ubind", 1);
        args.put("ucontrol", 6);
        args.put("getlink", 2);
        args.put("printflush", 1);
        args.put("uradar", 7);
        args.put("ulocate", 8);
        FN_ARGS = Collections.unmodifiableMap(args);
    }

    public static String toString(List<LogicInstruction> instructions) {
        final StringBuilder buffer = new StringBuilder();
        instructions.forEach((instruction) -> {
            if (!FN_ARGS.containsKey(instruction.getOpcode())) {
                throw new GenerationException("Don't know how many arguments [" + instruction.getOpcode() + "] needs; add it to FN_ARGS");
            }
            buffer.append(instruction.getOpcode());

            int count = FN_ARGS.get(instruction.getOpcode());
            if (count > 0) buffer.append(" ");
            addArgs(count, buffer, instruction);
        });

        return buffer.toString();
    }

    private static void addArgs(int count, StringBuilder buffer, LogicInstruction instruction) {
        for (int i = 0; i < count; i++) {
            if (instruction.getArgs().size() > i) {
                buffer.append(instruction.getArgs().get(i));
            } else {
                buffer.append("0");
            }

            if (i < count - 1) buffer.append(" ");
        }

        buffer.append("\n");
    }
}
