package info.teksol.mindcode.compiler;

import info.teksol.mindcode.compiler.instructions.AstContext;
import info.teksol.mindcode.compiler.instructions.AstSubcontextType;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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

    public static String toStringWithContextsFull(InstructionProcessor instructionProcessor, List<LogicInstruction> instructions) {
        DecimalFormat format = new DecimalFormat("0.0E00");
        format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
        format.setMaximumFractionDigits(2);

        final StringBuilder buffer = new StringBuilder();
        instructions.forEach((instruction) -> {
            LinkedList<AstContext> unroll = new LinkedList<>();
            for (AstContext ctx = instruction.getAstContext(); ctx != null; ctx = ctx.parent()) {
                if (ctx.subcontextType() == (ctx.node() == null ?  AstSubcontextType.BASIC : ctx.node().getSubcontextType())) {
                    unroll.addFirst(ctx);
                }
            }

            String hierarchy = unroll.stream().limit(10).map(c -> c.contextType().text).collect(Collectors.joining(" "));
            AstContext ctx = instruction.getAstContext();
            buffer.append("%-50s  %s  %10s ".formatted(hierarchy, ctx.subcontextType().text, format.format(ctx.totalWeight())));
            buffer.append(instruction.getOpcode().getOpcode());
            addArgs(instructionProcessor.getPrintArgumentCount(instruction), buffer, instruction);
            buffer.append("\n");
        });

        return buffer.toString();
    }

    public static String toStringWithContextsShort(InstructionProcessor instructionProcessor, List<LogicInstruction> instructions) {
        DecimalFormat format = new DecimalFormat("0.0E00");
        format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
        format.setMaximumFractionDigits(2);

        final StringBuilder buffer = new StringBuilder();
        instructions.forEach((instruction) -> {
            AstContext ctx = instruction.getAstContext();
            buffer.append("%3d:%s  %s %8s ".formatted(ctx.level(), ctx.contextType().text,
                    ctx.subcontextType().text, format.format(ctx.totalWeight())));
            buffer.append(instruction.getOpcode().getOpcode());
            addArgs(instructionProcessor.getPrintArgumentCount(instruction), buffer, instruction);
            buffer.append("\n");
        });

        return buffer.toString();
    }

    public static String toStringWithSourceCode(InstructionProcessor instructionProcessor, List<LogicInstruction> instructions, String sourceCode) {
        final List<String> lines = sourceCode.lines().toList();
        int prevLine = -1;

        final StringBuilder buffer = new StringBuilder();
        final StringBuilder lineBuffer = new StringBuilder();

        for (LogicInstruction instruction : instructions) {
            lineBuffer.setLength(0);
            lineBuffer.append(instruction.getOpcode().getOpcode());
            addArgs(instructionProcessor.getPrintArgumentCount(instruction), lineBuffer, instruction);

            if (instruction.getAstContext().node() != null && instruction.getAstContext().node().startToken() != null) {
                String srcLine = "** Corresponding source code line not found! **";
                int line = instruction.getAstContext().node().startToken().getLine() - 1;
                if (line == prevLine) {
                    srcLine = "...";
                } else if (line >= 0 && line < lines.size()) {
                    srcLine = lines.get(line).trim();
                    prevLine = line;
                }

                int oldLen = lineBuffer.length();
                if (lineBuffer.length() < 60) {
                    lineBuffer.setLength(60);
                    for (int i = oldLen; i < 60; i++) lineBuffer.setCharAt(i, ' ');
                } else {
                    lineBuffer.setLength(60);
                }
                lineBuffer.append(' ').append(srcLine);
            }

            buffer.append(lineBuffer).append("\n");
        }

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
