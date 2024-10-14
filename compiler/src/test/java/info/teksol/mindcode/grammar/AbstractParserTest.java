package info.teksol.mindcode.grammar;

import info.teksol.mindcode.MindcodeErrorListener;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.MindcodeMessage;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractParserTest {

    protected MindcodeParser.ProgramContext parse(String code) {
        final List<MindcodeMessage> errors = new ArrayList<>();
        MindcodeErrorListener errorListener = new MindcodeErrorListener(errors);

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

    List<MindcodeMessage> parseWithErrors(String program) {
        final List<MindcodeMessage> errors = new ArrayList<>();
        MindcodeErrorListener errorListener = new MindcodeErrorListener(errors);

        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(program));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);

        final MindcodeParser parser = new MindcodeParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        parser.program();
        return errors;
    }
}
