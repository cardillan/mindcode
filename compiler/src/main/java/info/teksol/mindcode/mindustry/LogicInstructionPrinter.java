package info.teksol.mindcode.mindustry;

import java.util.List;

public class LogicInstructionPrinter {
    public static String toString(List<LogicInstruction> instructions) {
        final StringBuilder buffer = new StringBuilder();
        instructions.forEach((instruction) -> {
            buffer.append(instruction.getOpcode().getOpcode());
            addArgs(instruction.getOpcode().getArgumentCount(), buffer, instruction);
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
