package info.teksol.mindcode;

import org.antlr.v4.runtime.Token;
import org.intellij.lang.annotations.PrintFormat;

public class MindcodeException extends RuntimeException {
    public MindcodeException(String message) {
        super(message);
    }

    private MindcodeException(Token token, String message) {
        this(token != null
                ? String.format("Syntax error at %d:%d: %s", token.getLine(), token.getCharPositionInLine(), message)
                : message);
    }

    public MindcodeException(Token token, @PrintFormat String message, Object... args) {
        this(token, String.format(message, args));
    }
}
