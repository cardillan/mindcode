package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.mindcode.compiler.antlr.MindcodeLexer;
import info.teksol.mc.mindcode.compiler.antlr.MissingSemicolonException;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MindcodeErrorStrategy extends DefaultErrorStrategy {
    private final CommonTokenStream tokenStream;

    public MindcodeErrorStrategy(CommonTokenStream tokenStream) {
        this.tokenStream = tokenStream;
    }

    @Override
    protected void reportMissingToken(Parser recognizer) {
        if (this.inErrorRecoveryMode(recognizer)) return;

        IntervalSet expectedTokens = recognizer.getExpectedTokens();
        if (expectedTokens.size() == 1 && expectedTokens.contains(MindcodeLexer.SEMICOLON)) {
            this.beginErrorCondition(recognizer);
            Token t = recognizer.getCurrentToken();
            Token previous = tokenStream.LT(-1);
            IntervalSet expecting = this.getExpectedTokens(recognizer);
            String msg = "missing semicolon after " + this.getTokenErrorDisplay(previous);
            recognizer.notifyErrorListeners(previous, msg, new MissingSemicolonException(t));
        } else {
            super.reportMissingToken(recognizer);
        }
    }
}
