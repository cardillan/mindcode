package info.teksol.mc.mindcode.compiler.antlr;

import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;

public class MissingSemicolonException extends RecognitionException {
    private final Token nextToken;

    public MissingSemicolonException(Token nextToken) {
        super(null, null, null);
        this.nextToken = nextToken;
    }

    public Token getNextToken() {
        return nextToken;
    }
}
