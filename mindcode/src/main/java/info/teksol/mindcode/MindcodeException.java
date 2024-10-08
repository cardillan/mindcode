package info.teksol.mindcode;

import org.antlr.v4.runtime.Token;
import org.intellij.lang.annotations.PrintFormat;

public class MindcodeException extends RuntimeException {
    private final Token token;
    private boolean reported = false;

    private MindcodeException(Token token, String message) {
        super(message);
        this.token = token;
    }

    public MindcodeException(Token token, @PrintFormat String message, Object... args) {
        this(token, String.format(message, args));
    }

    public Token getToken() {
        return token;
    }

    public boolean isReported() {
        return reported;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }
}
