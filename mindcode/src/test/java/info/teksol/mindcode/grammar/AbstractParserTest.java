package info.teksol.mindcode.grammar;

import info.teksol.mindcode.MindcodeInternalError;
import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractParserTest {
    protected MindcodeParser.ProgramContext parse(String code) {
        final List<String> errors = new ArrayList<>();
        ErrorListener errorListener = new ErrorListener(errors);

        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(code));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);

        final MindcodeParser parser = new MindcodeParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        final MindcodeParser.ProgramContext context = parser.program();
        if (!errors.isEmpty()) {
            throw new MindcodeInternalError(errors.toString());
        }
        return context;
    }

    List<String> parseWithErrors(String program) {
        final List<String> errors = new ArrayList<>();
        ErrorListener errorListener = new ErrorListener(errors);

        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(program));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);

        final MindcodeParser parser = new MindcodeParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        parser.program();
        return errors;
    }

    private static class ErrorListener extends BaseErrorListener {
        private final List<String> errors;

        public ErrorListener(List<String> errors) {
            this.errors = errors;
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                String msg, RecognitionException e) {
            if (offendingSymbol == null) {
                errors.add("Syntax error on line " + line + ":" + charPositionInLine + ": " + msg);
            } else {
                errors.add("Syntax error: " + offendingSymbol + " on line " + line + ":" + charPositionInLine + ": " + msg);
            }
        }
    }
}
