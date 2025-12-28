package info.teksol.mc.mindcode.compiler.postprocess;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.profile.FinalCodeOutput;
import info.teksol.mc.util.Indenter;
import org.jspecify.annotations.NullMarked;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@NullMarked
public class LogicInstructionPrinter {

    /// Produces the final compiler output
    public static String toString(InstructionProcessor instructionProcessor, List<LogicInstruction> instructions,
            boolean symbolicLabels, int mlogIndent) {
        return toString(instructionProcessor, instructions, symbolicLabels, mlogIndent, i -> "");
    }

    public static String toStringWithProfiling(InstructionProcessor instructionProcessor, List<LogicInstruction> instructions,
            boolean symbolicLabels, int mlogIndent, int[] profile) {
        return toString(instructionProcessor, instructions, symbolicLabels, mlogIndent,
                i -> profile[i] >= 0 ? String.format("%6d: ", profile[i]) : "        ");
    }

    public static String toString(InstructionProcessor instructionProcessor, List<LogicInstruction> instructions,
            boolean symbolicLabels, int mlogIndent, Function<Integer, String> prefixSupplier) {
        final String prefix = symbolicLabels && mlogIndent == 0 ? "    " : "";
        final StringBuilder buffer = new StringBuilder();
        final Indenter indenter = new Indenter(" ".repeat(mlogIndent));

        for (int i = 0; i < instructions.size(); i++) {
            LogicInstruction instruction = instructions.get(i);
            buffer.append(prefixSupplier.apply(i));
            buffer.append(indenter.getIndent(indent(instruction, symbolicLabels)));

            if (instruction instanceof CommentInstruction rem) {
                buffer.append(rem.toMlog());
            } else if (instruction instanceof LabeledInstruction label) {
                buffer.append(label.getLabel().toMlog()).append(":");
            } else {
                buffer.append(prefix);
                addInstruction(buffer, instruction, instructionProcessor.getPrintArgumentCount(instruction));
            }
            buffer.append("\n");
        }

        return buffer.toString();
    }

    private static int indent(LogicInstruction instruction, boolean symbolicLabels) {
        int indent = 0;
        for (AstContext ctx = instruction.getAstContext(); ctx != null; ctx = ctx.parent()) {
            if (ctx.subcontextType() == AstSubcontextType.BODY || ctx.subcontextType() == AstSubcontextType.FLOW_CONTROL) {
                indent++;
            }
        }
        return Math.max(0, indent - (instruction instanceof LabelInstruction ? 1 : 0) - (symbolicLabels ? 0 : 1)) ;
    }

    public static String toString(FinalCodeOutput finalCodeOutput, InstructionProcessor instructionProcessor,
            List<LogicInstruction> instructions) {
        return switch (finalCodeOutput) {
            case PLAIN      -> LogicInstructionPrinter.toStringWithLineNumbers(instructionProcessor, instructions);
            case FLAT_AST   -> LogicInstructionPrinter.toStringWithContextsShort(instructionProcessor, instructions);
            case DEEP_AST   -> LogicInstructionPrinter.toStringWithContextsFull(instructionProcessor, instructions);
            case SOURCE     -> LogicInstructionPrinter.toStringWithSourceCode(instructionProcessor, instructions);
            default         -> throw new MindcodeInternalError("Unsupported FinalCodeOutput: " + finalCodeOutput);
        };
    }

    public static String toStringWithLineNumbers(InstructionProcessor instructionProcessor, List<LogicInstruction> instructions) {
        final StringBuilder buffer = new StringBuilder();
        RealLineNumberGenerator lineNumberGenerator = new RealLineNumberGenerator();
        instructions.forEach(instruction -> {
            buffer.append(lineNumberGenerator.printLineNumber(instruction, ""));
            addInstruction(buffer, instruction, instructionProcessor.getPrintArgumentCount(instruction));
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
            addInstruction(buffer, instruction, instructionProcessor.getPrintArgumentCount(instruction));
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
            addInstruction(buffer, instruction, instructionProcessor.getPrintArgumentCount(instruction));
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
            addInstruction(lineBuffer, instruction, instructionProcessor.getPrintArgumentCount(instruction));

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
        addInstruction(buffer, instruction, instructionProcessor.getPrintArgumentCount(instruction));
        return buffer.toString();
    }

    public static String toStringSimple(MlogInstruction instruction) {
        final StringBuilder buffer = new StringBuilder();
        addInstruction(buffer, instruction, instruction.getArgs().size());
        return buffer.toString();
    }

    private static final int COMMENT_COLUMN = 39;
    private static final String SPACES = " ".repeat(COMMENT_COLUMN);

    private static void addInstruction(StringBuilder buffer, MlogInstruction instruction, int argumentCount) {
        int start = buffer.length();

        buffer.append(instruction.getMlogOpcode());
        for (int i = 0; i < argumentCount; i++) {
            buffer.append(" ");
            if (instruction.getArgs().size() > i) {
                buffer.append(instruction.getArg(i).toMlog());
            } else {
                buffer.append("0");
            }
        }

        if (instruction instanceof LogicInstruction ix && !ix.getComment().isEmpty()) {
            int length = COMMENT_COLUMN - (buffer.length() - start);
            if (length > 0) buffer.append(SPACES, 0, length);
            buffer.append(" ");
            buffer.append(ix.getComment());
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
            if (!instruction.isReal() || instruction instanceof CustomInstruction cix && cix.isLabel()) {
                result = "%5s%s   ".formatted("", decoration);
                lastRemark = false;
            } else {
                result = "%5d%s:  ".formatted(lineNumber, decoration);
                if (instruction instanceof RemarkInstruction && instruction.getRealSize() == 2) {
                    lineNumber += lastRemark ? 1 : 2;
                    lastRemark = true;
                } else {
                    lineNumber += instruction.getRealSize();
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
