package info.teksol.mindcode.grammar;

import info.teksol.mindcode.MindcodeErrorListener;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.ExpectedMessages;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractParserTest {

    protected ThreadLocal<Integer> parseAmbiguities = ThreadLocal.withInitial(() -> 0);

    protected MindcodeParser.ProgramContext parse(Consumer<MindcodeMessage> messageConsumer, String code) {
        MindcodeErrorListener errorListener = new MindcodeErrorListener(messageConsumer);

        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(code));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);

        final MindcodeParser parser = new MindcodeParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        final MindcodeParser.ProgramContext context = parser.program();

        // Ugly hack
        parseAmbiguities.set(errorListener.getAmbiguities());
        return context;
    }

    protected MindcodeParser.ProgramContext parse(String code) {
        return parse(ExpectedMessages.throwOnMessage(),code);
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
