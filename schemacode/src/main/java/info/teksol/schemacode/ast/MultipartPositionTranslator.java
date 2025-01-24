package info.teksol.schemacode.ast;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.SourcePositionTranslator;
import info.teksol.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
class MultipartPositionTranslator implements SourcePositionTranslator {
    record Part(int newLines, int lastLineLength, InputFile inputFile, int lineOffset, int columnOffset) {}
    private final List<Part> parts;

    public static SourcePositionTranslator createTranslator(SchematicsBuilder builder, List<AstProgramSnippet> snippets) {
        return switch (snippets.size()) {
            case 0  -> p -> p;
            case 1  -> createSimpleTranslator(builder, snippets.getFirst());
            default -> new MultipartPositionTranslator(snippets.stream().map(s -> createPart(builder, s)).toList());
        };
    }

    MultipartPositionTranslator(List<Part> parts) {
        this.parts = parts;
    }

    private static class Position {
        // Zero based
        int line;
        int column;

        Position(SourcePosition sourcePosition) {
            line = sourcePosition.line() - 1;
            column = sourcePosition.column() - 1;
        }

        boolean isAfter(Part part) {
            return line > part.newLines || line == part.newLines && column >= part.lastLineLength;
        }

        void subtract(Part part) {
            if (line < part.newLines) {
                throw new IllegalStateException("Part has more lines than position, cannot subtract");
            } else if (line > part.newLines) {
                line -= part.newLines;
            } else if (column < part.lastLineLength) {
                throw new IllegalStateException("Part has more columns than position, cannot subtract");
            } else {
                line = 0;
                column -= part.lastLineLength;
            }
        }

        @Override
        public String toString() {
            return line + ":" + column;
        }
    }

    @Override
    public SourcePosition apply(SourcePosition position) {
        Position pos = new Position(position);
        int index = 0;

        while (index < parts.size() - 1 && pos.isAfter(parts.get(index))) {
            pos.subtract(parts.get(index));
            index++;
        }

        Part part = parts.get(index);
        return new SourcePosition(part.inputFile, pos.line + part.lineOffset + 1, pos.column + part.columnOffset + 1);
    }

    private static Part createPart(SchematicsBuilder builder, AstProgramSnippet snippet) {
        String text = snippet.getProgramText(builder) + "\n";
        int lines = 0;
        int pos = 0;
        int lastPos = 0;
        while ((pos = text.indexOf('\n', pos) + 1) != 0) {
            lastPos = pos;
            lines++;
        }
        int lastLineLength = text.length() - lastPos;

        final SourcePosition source = snippet.getSourcePosition(builder);
        final InputFile inputFile = source.inputFile();
        final int lineOffset = source.line() - 1;
        final int columnOffset = snippet.getIndent(builder) + source.column() - 1;
        return new Part(lines, lastLineLength, inputFile, lineOffset, columnOffset);
    }

    private static SourcePositionTranslator createSimpleTranslator(SchematicsBuilder builder, AstProgramSnippet snippet) {
        final SourcePosition source = snippet.getSourcePosition(builder);
        final InputFile inputFile = source.inputFile();
        final int lineOffset = source.line() - 1;
        final int columnOffset = snippet.getIndent(builder) + source.column() - 1;
        return p ->
                new SourcePosition(source.inputFile(), p.line() + lineOffset,
                p.column() + columnOffset);
    }
}
