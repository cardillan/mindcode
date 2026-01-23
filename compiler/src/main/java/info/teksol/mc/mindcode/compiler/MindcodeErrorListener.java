package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.antlr.MissingSemicolonException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

@NullMarked
public class MindcodeErrorListener extends BaseErrorListener {
    private final MessageConsumer messageConsumer;
    private final InputFile inputFile;
    private int ambiguities = 0;

    public MindcodeErrorListener(MessageConsumer messageConsumer, InputFile inputFile) {
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
            String msg, @Nullable RecognitionException exception) {
        if (exception instanceof MissingSemicolonException ex && offendingSymbol instanceof Token token) {
            String nextToken = ex.getNextToken().getText();
            int length = token.getStopIndex() - token.getStartIndex();
            reportError(line, charPositionInLine + length + 1, "Parse error: %s",msg);
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

    private @Nullable String getOffendingTokenText(@Nullable RecognitionException ex) {
        return ex != null && ex.getOffendingToken() != null ? ex.getOffendingToken().getText() : null;
    }
}
