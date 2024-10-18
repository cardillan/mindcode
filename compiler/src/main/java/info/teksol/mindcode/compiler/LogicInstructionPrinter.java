package info.teksol.mindcode.compiler;

import info.teksol.mindcode.InputFile;
import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.MlogInstruction;
import info.teksol.mindcode.compiler.instructions.RemarkInstruction;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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

    private static class LineNumberGenerator {
        private final StringBuilder buffer;
        private int lineNumber = 0;
        private boolean lastRemark = false;

        LineNumberGenerator(StringBuilder buffer) {
            this.buffer = buffer;
        }

        void printLineNumber(LogicInstruction instruction) {
            if (instruction.getRealSize() == 0) {
                buffer.append("        ");
                lastRemark = false;
            } else {
                buffer.append("%5d:  ".formatted(lineNumber));
                if (instruction instanceof RemarkInstruction && instruction.getRealSize() == 2) {
                    lineNumber += lastRemark ? 1 : 2;
                    lastRemark = true;
                } else {
                    lineNumber += instruction.getRealSize();
                    lastRemark = false;
                }
            }
        }
    }

    public static String toStringWithLineNumbers(InstructionProcessor instructionProcessor, List<LogicInstruction> instructions) {
        final StringBuilder buffer = new StringBuilder();
        LineNumberGenerator lineNumberGenerator = new LineNumberGenerator(buffer);
        instructions.forEach(instruction -> {
            lineNumberGenerator.printLineNumber(instruction);
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
        LineNumberGenerator lineNumberGenerator = new LineNumberGenerator(buffer);
        instructions.forEach(instruction -> {
            lineNumberGenerator.printLineNumber(instruction);
            LinkedList<AstContext> unroll = new LinkedList<>();
            for (AstContext ctx = instruction.getAstContext(); ctx != null; ctx = ctx.parent()) {
                if (ctx.subcontextType() == (ctx.node() == null ? AstSubcontextType.BASIC : ctx.node().getSubcontextType())) {
                    unroll.addFirst(ctx);
                }
            }

            String hierarchy = unroll.stream().limit(10).map(c -> c.contextType().text).collect(Collectors.joining(" "));
            AstContext ctx = instruction.getAstContext();
            buffer.append("%-50s  %s (%3d)  %10s  ".formatted(hierarchy, ctx.subcontextType().text, ctx.id, format.format(ctx.totalWeight())));
            buffer.append(instruction.getOpcode().getOpcode());
            addArgs(instructionProcessor.getPrintArgumentCount(instruction), buffer, instruction);
            buffer.append("\n");
        });

        return buffer.toString();
    }

    public static String toStringWithContextsShort(InstructionProcessor instructionProcessor, List<LogicInstruction> instructions) {
        AtomicInteger lineNumber = new AtomicInteger(0);
        DecimalFormat format = new DecimalFormat("0.0E00");
        format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
        format.setMaximumFractionDigits(2);

        final StringBuilder buffer = new StringBuilder();
        LineNumberGenerator lineNumberGenerator = new LineNumberGenerator(buffer);
        instructions.forEach(instruction -> {
            lineNumberGenerator.printLineNumber(instruction);
            AstContext ctx = instruction.getAstContext();
            buffer.append("%3d:%s  %s %8s ".formatted(ctx.level(), ctx.contextType().text,
                    ctx.subcontextType().text, format.format(ctx.totalWeight())));
            buffer.append(instruction.getOpcode().getOpcode());
            addArgs(instructionProcessor.getPrintArgumentCount(instruction), buffer, instruction);
            buffer.append("\n");
        });

        return buffer.toString();
    }

    public static String toStringWithSourceCode(InstructionProcessor instructionProcessor, List<LogicInstruction> instructions) {
        AtomicInteger lineNumber = new AtomicInteger(0);
        final Map<InputFile, List<String>> allLines = new HashMap<>();
        int prevLine = -1;

        final StringBuilder buffer = new StringBuilder();
        final StringBuilder lineBuffer = new StringBuilder();
        LineNumberGenerator lineNumberGenerator = new LineNumberGenerator(buffer);

        for (LogicInstruction instruction : instructions) {
            lineNumberGenerator.printLineNumber(instruction);
            lineBuffer.setLength(0);
            lineBuffer.append(instruction.getOpcode().getOpcode());
            addArgs(instructionProcessor.getPrintArgumentCount(instruction), lineBuffer, instruction);

            AstContext astContext = instruction.getAstContext();
            if (astContext.node() != null && astContext.node().getInputPosition() != null) {
                InputFile file = astContext.node().getInputPosition().inputFile();
                String srcLine = "** Corresponding source code line not found! **";
                List<String> lines = allLines.get(file);
                if (lines == null) {
                    lines = file.code().lines().toList();
                    allLines.put(file, lines);
                }

                int line = astContext.node().getInputPosition().line() - 1;
                if (line == prevLine) {
                    srcLine = "...";
                } else if (line >= 0 && line < lines.size()) {
                    srcLine = (file.fileName().isEmpty() ? "" : file.fileName() + ": ") + lines.get(line).trim();
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

    public static String toStringSimple(MlogInstruction instruction) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(instruction.getOpcode().getOpcode());
        addArgs(instruction.getArgs().size(), buffer, instruction);
        return buffer.toString();
    }

    private static void addArgs(int count, StringBuilder buffer, MlogInstruction instruction) {
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
