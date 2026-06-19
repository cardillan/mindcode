package info.teksol.mc.common;

import info.teksol.mc.profile.FileReferences;
import org.antlr.v4.runtime.Token;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Comparator;

/// Represents a position in a source file. The position has three parts: start, end and token.
/// Token must lie between start and end (inclusive). Token is most often equal to start, albeit in some cases,
/// it can lie between start and end (e.g., in a binary expression, start and end correspond to the entire
/// expression, while token corresponds to the position of the operator).
///
/// When start is equal to end, the position marks a single specific spot in the input file (e.g., a position
/// of a missing semicolon).
@NullMarked
public record SourcePosition(InputFile inputFile,
                             TextFilePosition start,
                             TextFilePosition end,
                             TextFilePosition token) implements Comparable<SourcePosition> {

    public static final SourcePosition EMPTY = new SourcePosition(InputFile.EMPTY, 1, 1);

    public SourcePosition(InputFile inputFile, int line, int column) {
        this(inputFile, new TextFilePosition(line, column), new TextFilePosition(line, column),
                new TextFilePosition(line, column));
    }

    /// When reporting errors at the start of the line, it checks the line number doesn't exceed the number of lines
    /// in the source code. Used for parser errors, as an additional newline is added for parsing and
    public static SourcePosition createChecked(InputFile inputFile, int line, int column) {
        return new SourcePosition(inputFile, column == 1 && line == inputFile.getNumberOfLines() + 1 ? line - 1 : line, column);
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

    public String formatForIde(FileReferences fileReferences) {
        return inputFile.getPositionTranslator().apply(this).formatForIdeTranslated(fileReferences);
    }

    private String formatForIdeTranslated(FileReferences fileReferences) {
        return ((inputFile == InputFile.EMPTY || inputFile.getPath() == InputFiles.EMPTY_PATH ? "" : reference(fileReferences, inputFile.getPath())) + ":")
                + line() + ":" + column();
    }

    private String reference(FileReferences fileReferences, Path path) {
        return switch(fileReferences) {
            case PATH -> path.toAbsolutePath().toString();
            case URI -> path.toUri().normalize().toString();
            case WINDOWS_URI -> toWindowsUri(path.toUri().normalize().toString());
        };
    }

    private String toWindowsUri(String reference) {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            if (reference.startsWith("file:///")) return reference;
            if (reference.startsWith("file://")) return reference.replaceFirst("file://", "file:///");
            return reference.replaceFirst("file:/", "file:///");
        } else {
            return reference;
        }
    }

    public String formatForLog() {
        String distinctPath = inputFile == InputFile.EMPTY ? "" : inputFile.getDistinctPath();
        return distinctPath.isEmpty() ? "line " + line() + ":" + column() : distinctPath + ":" + line() + ":" + column();
    }

    public String formatForMlog() {
        String distinctPath = inputFile == InputFile.EMPTY ? "" : inputFile.getDistinctPath();
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
        return inputFile == InputFile.EMPTY;
    }

    public String getDistinctPath() {
        return inputFile == InputFile.EMPTY ? "" : inputFile.getDistinctPath();
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

    public SourcePosition offsetLine(int offset) {
        return new SourcePosition(inputFile, line() + offset, column());
    }

    @Override
    public int compareTo(SourcePosition o) {
        return COMPARATOR.compare(this, o);
    }

    private static final Comparator<SourcePosition> COMPARATOR = Comparator
            .<SourcePosition, @Nullable InputFile>comparing(SourcePosition::inputFile,
                    Comparator.nullsFirst(Comparator.<InputFile>comparingInt(InputFile::getId)))
            .thenComparingInt(SourcePosition::line)
            .thenComparingInt(SourcePosition::column);
}
