package info.teksol.mindcode.grammar;

import info.teksol.mindcode.Tuple2;
import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractParserTest {
    protected MindcodeParser.ProgramContext parse(String program) {
        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(program));
        final MindcodeParser parser = new MindcodeParser(new BufferedTokenStream(lexer));
        final List<String> errors = new ArrayList<>();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                errors.add("Syntax error: " + offendingSymbol + " on line " + line + ":" + charPositionInLine + ": " + msg);
            }
        });

        final MindcodeParser.ProgramContext context = parser.program();
        assertEquals(0, errors.size(), errors.toString());
        return context;
    }

    Tuple2<MindcodeParser.ProgramContext, List<String>> parseWithErrors(String program) {
        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(program));
        final MindcodeParser parser = new MindcodeParser(new BufferedTokenStream(lexer));
        final List<String> errors = new ArrayList<>();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                errors.add("Syntax error: " + offendingSymbol + " on line " + line + ":" + charPositionInLine + ": " + msg);
            }
        });

        final MindcodeParser.ProgramContext context = parser.program();
        return new Tuple2<>(context, errors);
    }
}
