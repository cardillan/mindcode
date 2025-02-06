package info.teksol.mc.common;

import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Comparator;

/// Represents a position in a source file. The position has three components: start, end and token.
/// Token must lie between start and end (inclusive). Token is most often equal to start, albeit in some cases,
///  it can lie between start and end (e.g. in a binary expression, start and end correspond to the entire
/// expression, while token corresponds to the position of the operator).
///
/// When start is equal to end, the position marks a single specific spot in the input file (e.g. a position
/// of a missing semicolon).
@NullMarked
public record SourcePosition(InputFile inputFile,
                             TextFilePosition start,
                             TextFilePosition end,
                             TextFilePosition token) implements Comparable<SourcePosition> {

    private static final InputFile EMPTY_INPUT_FILE = new InputFile() {
        @Override public int getId() { return Integer.MIN_VALUE; }
        @Override public boolean isStandaloneSource() { return false; }
        @Override public boolean isLibrary() { return false; }
        @Override public String getCode() { return ""; }
        @Override public Path getPath() { return Path.of(""); }
        @Override public Path getRelativePath() { return Path.of(""); }
        @Override public String getAbsolutePath() { return ""; }
        @Override public String getDistinctPath() { return ""; }
        @Override public String getDistinctTitle() { return ""; }
    };

    public static SourcePosition EMPTY = new SourcePosition(EMPTY_INPUT_FILE, 1, 1);

    public SourcePosition(InputFile inputFile, int line, int column) {
        this(inputFile, new TextFilePosition(line, column), new TextFilePosition(line, column),
                new TextFilePosition(line, column));
    }

    public SourcePosition upTo(SourcePosition end) {
        return new SourcePosition(inputFile, start, end.end, token);
    }

    public int line() {
        return token.line();
    }

    public int column() {
        return token.column();
    }

    public String formatForIde() {
        return (inputFile == EMPTY_INPUT_FILE ? "" : inputFile.getAbsolutePath()) + ":" + line() + ":" + column();
    }

    public String formatForLog() {
        String distinctPath = inputFile == EMPTY_INPUT_FILE ? "" : inputFile.getDistinctPath();
        return distinctPath.isEmpty() ? "line " + line() + ":" + column() : distinctPath + ":" + line() + ":" + column();
    }

    public String formatForMlog() {
        String distinctPath = inputFile == EMPTY_INPUT_FILE ? "" : inputFile.getDistinctPath();
        return distinctPath.isEmpty() ? "position " + line() + ":" + column() : distinctPath + ":" + line() + ":" + column();
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
        return inputFile == EMPTY_INPUT_FILE;
    }

    public String getDistinctPath() {
        return inputFile == EMPTY_INPUT_FILE ? "" : inputFile.getDistinctPath();
    }

    public boolean isLibrary() {
        return inputFile.isLibrary();
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
