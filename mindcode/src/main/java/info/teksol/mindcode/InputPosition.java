package info.teksol.mindcode;

import org.antlr.v4.runtime.Token;

import java.util.Objects;

public record InputPosition(InputFile inputFile, int line, int charPositionInLine) {

    public static InputPosition EMPTY = new InputPosition(InputFile.EMPTY, 0, 0);

    public InputPosition {
        Objects.requireNonNull(inputFile);
    }

    public String formatForIde() {
        return inputFile.absolutePath() + ":" + line + ":" + charPositionInLine;
    }

    public static InputPosition create(InputFile inputFile, Token token) {
        return new InputPosition(inputFile, token.getLine(), token.getCharPositionInLine() + 1);
    }

    public boolean isEmpty() {
        return inputFile.isEmpty();
    }
}
