package info.teksol.mindcode.v3;

import info.teksol.mindcode.ToolMessage;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeLexer;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParser;
import info.teksol.mindcode.v3.compiler.antlr.MindcodeParser.AstModuleContext;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LexerParser {

    public static CommonTokenStream createTokenStream(MessageConsumer messageConsumer, InputFile inputFile) {
        MindcodeErrorListener errorListener = new MindcodeErrorListener(messageConsumer, inputFile);
        // We're adding a newline at the end, because it makes some grammar definitions way easier
        MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(inputFile.getCode() + "\n"));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        return new CommonTokenStream(lexer);
    }

    public static AstModuleContext parseTree(MessageConsumer messageConsumer, InputFile inputFile,
            CommonTokenStream tokenStream) {
        MindcodeErrorListener errorListener = new MindcodeErrorListener(messageConsumer, inputFile);
        MindcodeParser parser = new MindcodeParser(tokenStream);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        parser.setErrorHandler(new MindcodeErrorStrategy(tokenStream));
        AstModuleContext parseTree = parser.astModule();
        messageConsumer.accept(ToolMessage.info("%s: number of reported ambiguities: %d",
                inputFile.getDistinctTitle(), errorListener.getAmbiguities()));
        return parseTree;
    }

}
