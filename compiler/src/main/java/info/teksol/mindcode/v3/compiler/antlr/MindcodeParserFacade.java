package info.teksol.mindcode.v3.compiler.antlr;

import info.teksol.mindcode.MindcodeErrorListener;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ToolMessage;
import info.teksol.mindcode.v3.InputFile;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.function.Consumer;

/**
 * Facade for ANTLR4 lexer and parser. Parses an input file into a parse tree represented by the root node.
 */
public class MindcodeParserFacade {

    public static MindcodeParser.ProgramContext parse(Consumer<MindcodeMessage> messageConsumer, InputFile inputFile) {
        MindcodeErrorListener errorListener = new MindcodeErrorListener(messageConsumer, inputFile);
        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(inputFile.getCode() + "\n"));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        final MindcodeParser parser = new MindcodeParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        MindcodeParser.ProgramContext program = parser.program();
        messageConsumer.accept(ToolMessage.info("%s: number of reported ambiguities: %d",
                inputFile.getDistinctTitle(), errorListener.getAmbiguities()));
        return program;
    }

}
