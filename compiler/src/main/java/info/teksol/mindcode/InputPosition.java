package info.teksol.mindcode;

import info.teksol.mindcode.v3.InputFile;
import org.antlr.v4.runtime.Token;

public record InputPosition(InputFile inputFile, int line, int column) {

    public static InputPosition EMPTY = new InputPosition(null, 0, 0);

    public String formatForIde() {
        return (inputFile == null ? "" : inputFile.getAbsolutePath()) + ":" + line + ":" + column;
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
