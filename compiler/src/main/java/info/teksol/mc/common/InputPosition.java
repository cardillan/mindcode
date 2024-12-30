package info.teksol.mc.common;

import org.antlr.v4.runtime.Token;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record InputPosition(@Nullable InputFile inputFile, int line, int column) {

    public static InputPosition EMPTY = new InputPosition(null, 0, 0);

    public String formatForIde() {
        return (inputFile == null ? "" : inputFile.getAbsolutePath()) + ":" + line + ":" + column;
    }

    public String formatForLog() {
        String distinctPath = inputFile == null ? "" : inputFile.getDistinctPath();
        return distinctPath.isEmpty() ? "line " + line + ":" + column : distinctPath + ":" + line + ":" + column;
    }

    public static InputPosition create(InputFile inputFile, Token token) {
        return new InputPosition(inputFile, token.getLine(), token.getCharPositionInLine() + 1);
    }

    public boolean isEmpty() {
        return inputFile == null;
    }

    public InputPosition withColumn(int column) {
        return new InputPosition(inputFile, line, column);
    }

    public InputPosition nextLine() {
        return new InputPosition(inputFile, line + 1, 1);
    }
}
