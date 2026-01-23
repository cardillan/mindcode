package info.teksol.schemacode;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.PositionalMessage;
import info.teksol.mc.mindcode.compiler.antlr.MissingSemicolonException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.intellij.lang.annotations.PrintFormat;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

public class SchemacodeErrorListener extends BaseErrorListener {
    private final MessageConsumer messageConsumer;
    private final InputFile inputFile;
    private int ambiguities = 0;

    public SchemacodeErrorListener(MessageConsumer messageConsumer, InputFile inputFile) {
        this.messageConsumer = messageConsumer;
        this.inputFile = inputFile;
    }

    public int getAmbiguities() {
        return ambiguities;
    }

    private final Set<PositionalMessage> reportedMessages = new HashSet<>();

    private void reportError(int line, int charPositionInLine, @PrintFormat String format, Object... args) {
        PositionalMessage message = PositionalMessage.error(
                new SourcePosition(inputFile, line, charPositionInLine + 1), format, args);
        if (reportedMessages.add(message)) {
            messageConsumer.accept(message);
        }
    }

    @Override
    public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
        ambiguities++;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException exception) {
        if (exception instanceof MissingSemicolonException ex && offendingSymbol instanceof Token token) {
            String nextToken = ex.getNextToken().getText();
            // Reporting missing semicolon before "do" and "then" is probably a consequence of do/then being
            // driven by syntactic predicate. In any case it doesn't make any sense.
            if (!"do".equals(nextToken) && !"then".equals(nextToken)) {
                int length = token.getStopIndex() - token.getStartIndex();
                reportError(line, charPositionInLine + length + 1, "Parse error: %s%s",
                        msg, ex.getNextToken() != null ? " before '" + nextToken + "'" : "");

            }
        } else if (exception instanceof NoViableAltException) {
            String offendingTokenText = getOffendingTokenText(exception);
            if (offendingTokenText == null) {
                reportError(line, charPositionInLine, "Parse error: unrecoverable parse error");
            } else {
                reportError(line, charPositionInLine, "Parse error: unexpected '%s'", offendingTokenText);
            }
        } else {
            String offendingTokenText = getOffendingTokenText(exception);
            if (offendingTokenText == null) {
                reportError(line, charPositionInLine, "Parse error: %s", msg);
            } else {
                reportError(line, charPositionInLine, "Parse error: '%s': %s", offendingTokenText, msg);
            }
        }
    }

    private String getOffendingTokenText(RecognitionException ex) {
        return ex != null && ex.getOffendingToken() != null ? ex.getOffendingToken().getText() : null;
    }
}
