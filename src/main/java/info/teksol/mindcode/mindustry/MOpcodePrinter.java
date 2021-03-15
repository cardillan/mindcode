package info.teksol.mindcode.mindustry;

import edu.emory.mathcs.backport.java.util.Collections;
import info.teksol.mindcode.GenerationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MOpcodePrinter {
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

        FN_ARGS = Collections.unmodifiableMap(args);
    }

    public static String toString(List<MOpcode> opcodes) {
        final StringBuilder buffer = new StringBuilder();
        opcodes.forEach((opcode) -> {
            if (!FN_ARGS.containsKey(opcode.getOpcode())) {
                throw new GenerationException("Don't know how many arguments [" + opcode.getOpcode() + "] needs; add it to FN_ARGS");
            }
            buffer.append(opcode.getOpcode());
            buffer.append(" ");
            addArgs(FN_ARGS.get(opcode.getOpcode()), buffer, opcode);
        });

        return buffer.toString();
    }

    private static void addArgs(int count, StringBuilder buffer, MOpcode opcode) {
        for (int i = 0; i < count; i++) {
            if (opcode.getArgs().size() > i) {
                buffer.append(opcode.getArgs().get(i));
            } else {
                buffer.append("0");
            }

            buffer.append(" ");
        }

        buffer.append("\n");
    }
}
