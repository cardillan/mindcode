package info.teksol.mindcode.mindustry;

import edu.emory.mathcs.backport.java.util.Collections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static info.teksol.mindcode.mindustry.Opcode.*;

public class LogicInstructionPrinter {
    private static final Map<Opcode, Integer> FN_ARGS;

    static {
        Map<Opcode, Integer> args = new HashMap<>();
        args.put(SET, 2);
        args.put(OP, 4);
        args.put(JUMP, 4);
        args.put(PRINT, 1);
        args.put(END, 0);
        args.put(SENSOR, 3);
        args.put(RADAR, 7);
        args.put(CONTROL, 6);
        args.put(DRAW, 7);
        args.put(WRITE, 3);
        args.put(READ, 3);
        args.put(WAIT, 1);
        args.put(DRAWFLUSH, 1);
        args.put(UBIND, 1);
        args.put(UCONTROL, 6);
        args.put(GETLINK, 2);
        args.put(PRINTFLUSH, 1);
        args.put(URADAR, 7);
        args.put(LABEL, 1);
        args.put(ULOCATE, 8);

        // This only serves to satisfy the compiler
        // In the generated, optimized code, this assignment should be elided
        @SuppressWarnings("unchecked") final Map<Opcode, Integer> tmp = Collections.unmodifiableMap(args);

        FN_ARGS = tmp;
    }

    public static String toString(List<LogicInstruction> instructions) {
        final StringBuilder buffer = new StringBuilder();
        instructions.forEach((instruction) -> {
            if (!FN_ARGS.containsKey(instruction.getOpcode())) {
                throw new GenerationException("Don't know how many arguments [" + instruction.getOpcode() + "] needs; add it to FN_ARGS");
            }
            buffer.append(instruction.getOpcode().getOpcode());
            addArgs(FN_ARGS.get(instruction.getOpcode()), buffer, instruction);
        });

        return buffer.toString();
    }

    private static void addArgs(int count, StringBuilder buffer, LogicInstruction instruction) {
        for (int i = 0; i < count; i++) {
            buffer.append(" ");
            if (instruction.getArgs().size() > i) {
                buffer.append(instruction.getArgs().get(i));
            } else {
                buffer.append("0");
            }
        }

        buffer.append("\n");
    }
}
