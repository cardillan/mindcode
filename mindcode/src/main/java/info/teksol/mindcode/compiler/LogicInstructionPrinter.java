package info.teksol.mindcode.compiler;

import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;

import java.util.List;

public class LogicInstructionPrinter {
    public static String toString(InstructionProcessor instructionProcessor, List<LogicInstruction> instructions) {
        final StringBuilder buffer = new StringBuilder();
        instructions.forEach((instruction) -> {
            buffer.append(instruction.getOpcode().getOpcode());
            addArgs(instructionProcessor.getPrintArgumentCount(instruction), buffer, instruction);
            buffer.append("\n");
        });

        return buffer.toString();
    }

    public static String toString(InstructionProcessor instructionProcessor, LogicInstruction instruction) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(instruction.getOpcode().getOpcode());
        addArgs(instructionProcessor.getPrintArgumentCount(instruction), buffer, instruction);
        return buffer.toString();
    }

    private static void addArgs(int count, StringBuilder buffer, LogicInstruction instruction) {
        for (int i = 0; i < count; i++) {
            buffer.append(" ");
            if (instruction.getArgs().size() > i) {
                buffer.append(instruction.getArg(i).toMlog());
            } else {
                buffer.append("0");
            }
        }
    }
}
