package info.teksol.mindcode;

import org.antlr.v4.runtime.Token;
import org.intellij.lang.annotations.PrintFormat;

public class MindcodeException extends RuntimeException {
    public MindcodeException(String message) {
        super(message);
    }

    public MindcodeException(Token token, @PrintFormat String message, Object... args) {
        super(String.format("Syntax error at %d:%d: %s", token.getLine(), token.getCharPositionInLine(),
                String.format(message, args)));
    }
}
