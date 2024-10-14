package info.teksol.mindcode;

import info.teksol.mindcode.compiler.MindcodeCompilerMessage;
import info.teksol.mindcode.grammar.MissingSemicolonException;
import org.antlr.v4.runtime.*;
import org.intellij.lang.annotations.PrintFormat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MindcodeErrorListener extends BaseErrorListener {
    private final List<MindcodeMessage> errors;
    private InputFile inputFile = InputFile.EMPTY;

    public MindcodeErrorListener(List<MindcodeMessage> errors) {
        this.errors = errors;
    }

    public void setInputFile(InputFile inputFile) {
        this.inputFile = inputFile;
    }

    private final Set<MindcodeCompilerMessage> reportedMessages = new HashSet<>();

    private void reportError(int line, int charPositionInLine, @PrintFormat String format, Object... args) {
        MindcodeCompilerMessage message = MindcodeCompilerMessage.error(
                new InputPosition(inputFile, line, charPositionInLine + 1), format, args);
        if (reportedMessages.add(message)) {
            errors.add(message);
        }
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
