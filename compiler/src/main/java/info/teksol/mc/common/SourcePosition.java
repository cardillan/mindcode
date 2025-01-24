package info.teksol.mc.common;

import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;

@NullMarked
public record SourcePosition(@Nullable InputFile inputFile,
                             TextFilePosition start,
                             TextFilePosition end,
                             TextFilePosition token) implements Comparable<SourcePosition> {

    public static SourcePosition EMPTY = new SourcePosition(null, 1, 1);

    public SourcePosition(@Nullable InputFile inputFile, int line, int column) {
        this(inputFile, new TextFilePosition(line, column), new TextFilePosition(line, column),
                new TextFilePosition(line, column));
    }

    public int line() {
        return token.line();
    }

    public int column() {
        return token.column();
    }

    public String formatForIde() {
        return (inputFile == null ? "" : inputFile.getAbsolutePath()) + ":" + line() + ":" + column();
    }

    public String formatForLog() {
        String distinctPath = inputFile == null ? "" : inputFile.getDistinctPath();
        return distinctPath.isEmpty() ? "line " + line() + ":" + column() : distinctPath + ":" + line() + ":" + column();
    }

    public static SourcePosition create(InputFile inputFile, Token token) {
        return new SourcePosition(inputFile,
                new TextFilePosition(token.getLine(), token.getCharPositionInLine() + 1),
                new TextFilePosition(token.getLine(), token.getCharPositionInLine() + token.getText().length() + 1),
                new TextFilePosition(token.getLine(), token.getCharPositionInLine() + 1));
    }

    public static SourcePosition create(InputFile inputFile, Token start, Token end, Token token) {
        return new SourcePosition(inputFile,
                new TextFilePosition(start.getLine(), start.getCharPositionInLine() + 1),
                new TextFilePosition(end.getLine(), end.getCharPositionInLine() + end.getText().length() + 1),
                new TextFilePosition(token.getLine(), token.getCharPositionInLine() + 1));
    }

    public boolean isEmpty() {
        return inputFile == null;
    }

    public String getDistinctPath() {
        return inputFile == null ? "" : inputFile.getDistinctPath();
    }

    public boolean isLibrary() {
        return inputFile != null && inputFile.isLibrary();
    }

    public SourcePosition withColumn(int column) {
        return new SourcePosition(inputFile, line(), column);
    }

    public SourcePosition nextLine() {
        return new SourcePosition(inputFile, line() + 1, 1);
    }

    @Override
    public int compareTo(@NotNull SourcePosition o) {
        return COMPARATOR.compare(this, o);
    }

    private static final Comparator<SourcePosition> COMPARATOR = Comparator
            .<SourcePosition, @Nullable InputFile>comparing(SourcePosition::inputFile,
                    Comparator.nullsFirst(Comparator.<InputFile>comparingInt(InputFile::getId)))
            .thenComparingInt(SourcePosition::line)
            .thenComparingInt(SourcePosition::column);
}
