package info.teksol.mindcode;

import org.antlr.v4.runtime.Token;

public record InputPosition(InputFile inputFile, int line, int charPositionInLine) {

    public String formatForIde() {
        return inputFile.absolutePath() + ":" + line + ":" + charPositionInLine;
    }

    public static InputPosition create(InputFile inputFile, Token token) {
        return new InputPosition(inputFile, token.getLine(), token.getCharPositionInLine() + 1);
    }

    public static InputPosition EMPTY = new InputPosition(
            new InputFile("", "", ""), 0, 0);
}
