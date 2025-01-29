package info.teksol.mc.mindcode.compiler.postprocess;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.MlogInstruction;
import info.teksol.mc.mindcode.logic.instructions.RemarkInstruction;
import info.teksol.mc.profile.FinalCodeOutput;
import org.jspecify.annotations.NullMarked;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@NullMarked
public class LogicInstructionPrinter {

    public static String toString(InstructionProcessor instructionProcessor, List<LogicInstruction> instructions) {
        final StringBuilder buffer = new StringBuilder();
        instructions.forEach((instruction) -> {
            buffer.append(instruction.getMlogOpcode());
            addArgs(instructionProcessor.getPrintArgumentCount(instruction), buffer, instruction);
            buffer.append("\n");
        });

        return buffer.toString();
    }

    public static String toString(FinalCodeOutput finalCodeOutput, InstructionProcessor instructionProcessor,
            List<LogicInstruction> instructions) {
        return switch (finalCodeOutput) {
            case PLAIN      -> LogicInstructionPrinter.toStringWithLineNumbers(instructionProcessor, instructions);
            case FLAT_AST   -> LogicInstructionPrinter.toStringWithContextsShort(instructionProcessor, instructions);
            case DEEP_AST   -> LogicInstructionPrinter.toStringWithContextsFull(instructionProcessor, instructions);
            case SOURCE     -> LogicInstructionPrinter.toStringWithSourceCode(instructionProcessor, instructions);
        };
    }

    public static String toStringWithLineNumbers(InstructionProcessor instructionProcessor, List<LogicInstruction> instructions) {
        final StringBuilder buffer = new StringBuilder();
        RealLineNumberGenerator lineNumberGenerator = new RealLineNumberGenerator();
        instructions.forEach(instruction -> {
            buffer.append(lineNumberGenerator.printLineNumber(instruction, ""));
            buffer.append(instruction.getMlogOpcode());
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
        RealLineNumberGenerator lineNumberGenerator = new RealLineNumberGenerator();
        instructions.forEach(instruction -> {
            buffer.append(lineNumberGenerator.printLineNumber(instruction, ""));
            LinkedList<AstContext> unroll = new LinkedList<>();
            for (AstContext ctx = instruction.getAstContext(); ctx != null; ctx = ctx.parent()) {
                if (ctx.subcontextType() == (ctx.node() == null ? AstSubcontextType.BASIC : ctx.node().getSubcontextType())) {
                    unroll.addFirst(ctx);
                }
            }

            String hierarchy = unroll.stream().limit(10).map(c -> c.contextType().text).collect(Collectors.joining(" "));
            AstContext ctx = instruction.getAstContext();
            buffer.append("%-50s  %s (%3d)  %10s  ".formatted(hierarchy, ctx.subcontextType().text, ctx.id, format.format(ctx.totalWeight())));
            buffer.append(instruction.getMlogOpcode());
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
        RealLineNumberGenerator lineNumberGenerator = new RealLineNumberGenerator();
        instructions.forEach(instruction -> {
            buffer.append(lineNumberGenerator.printLineNumber(instruction, ""));
            AstContext ctx = instruction.getAstContext();
            buffer.append("%3d:%s  %s %8s ".formatted(ctx.level(), ctx.contextType().text,
                    ctx.subcontextType().text, format.format(ctx.totalWeight())));
            buffer.append(instruction.getMlogOpcode());
            addArgs(instructionProcessor.getPrintArgumentCount(instruction), buffer, instruction);
            buffer.append("\n");
        });

        return buffer.toString();
    }

    public static String toStringWithSourceCode(InstructionProcessor instructionProcessor, List<LogicInstruction> instructions) {
        return toStringWithSourceCode(instructionProcessor, instructions, i -> "", new RealLineNumberGenerator());
    }

    public static String toStringWithSourceCode(InstructionProcessor instructionProcessor, List<LogicInstruction> instructions,
            Function<Integer, String> decorator) {
        return toStringWithSourceCode(instructionProcessor, instructions, decorator, new VirtualLineNumberGenerator());
    }

    private static String toStringWithSourceCode(InstructionProcessor instructionProcessor, List<LogicInstruction> instructions,
            Function<Integer, String> decorator, LineNumberGenerator lineNumberGenerator) {
        AtomicInteger lineNumber = new AtomicInteger(0);
        final Map<Integer, List<String>> allLines = new HashMap<>();
        int prevLine = -1;

        final StringBuilder buffer = new StringBuilder();
        final StringBuilder lineBuffer = new StringBuilder();

        int index = 0;
        for (LogicInstruction instruction : instructions) {
            buffer.append(lineNumberGenerator.printLineNumber(instruction, decorator.apply(index++)));
            lineBuffer.setLength(0);
            lineBuffer.append(instruction.getMlogOpcode());
            addArgs(instructionProcessor.getPrintArgumentCount(instruction), lineBuffer, instruction);

            AstContext astContext = instruction.getAstContext();
            if (astContext.node() != null && !astContext.node().sourcePosition().isEmpty()) {
                InputFile inputFile = astContext.node().sourcePosition().inputFile();
                String srcLine = "** Corresponding source code line not found! **";
                List<String> lines = allLines.get(inputFile.getId());
                if (lines == null) {
                    lines = inputFile.getCode().lines().toList();
                    allLines.put(inputFile.getId(), lines);
                }

                int line = astContext.node().sourcePosition().line() - 1;
                if (line == prevLine) {
                    srcLine = "...";
                } else if (line >= 0 && line < lines.size()) {
                    String strPath = inputFile.getDistinctPath();
                    srcLine = (strPath.isEmpty() ? "" : strPath + ": ") + lines.get(line).trim();
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
        buffer.append(instruction.getMlogOpcode());
        addArgs(instructionProcessor.getPrintArgumentCount(instruction), buffer, instruction);
        return buffer.toString();
    }

    public static String toStringSimple(MlogInstruction instruction) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(instruction.getMlogOpcode());
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

    private interface LineNumberGenerator {
        String printLineNumber(LogicInstruction instruction, String decoration);
    }

    private static class RealLineNumberGenerator implements LineNumberGenerator {
        private int lineNumber = 0;
        private boolean lastRemark = false;

        @Override
        public String printLineNumber(LogicInstruction instruction, String decoration) {
            String result;
            if (instruction.getRealSize(null) == 0) {
                result = "%5s%s   ".formatted("", decoration);
                lastRemark = false;
            } else {
                result = "%5d%s:  ".formatted(lineNumber, decoration);
                if (instruction instanceof RemarkInstruction && instruction.getRealSize(null) == 2) {
                    lineNumber += lastRemark ? 1 : 2;
                    lastRemark = true;
                } else {
                    lineNumber += instruction.getRealSize(null);
                    lastRemark = false;
                }
            }
            return result;
        }
    }

    private static class VirtualLineNumberGenerator implements LineNumberGenerator {
        private int lineNumber = 0;

        @Override
        public String printLineNumber(LogicInstruction instruction, String decoration) {
            return "%5d%s:  ".formatted(lineNumber++, decoration);
        }
    }
}
