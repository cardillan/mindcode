package info.teksol.mc.mindcode.compiler.antlr;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.messages.ToolMessage;
import info.teksol.mc.mindcode.compiler.MindcodeErrorListener;
import info.teksol.mc.mindcode.compiler.MindcodeErrorStrategy;
import info.teksol.mc.mindcode.compiler.antlr.MindcodeParser.AstModuleContext;
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
        if (errorListener.getAmbiguities() > 0) {
            messageConsumer.accept(ToolMessage.error(ERR.INTERNAL_AMBIGUOUS_CODE,
                    inputFile.getDistinctTitle(), errorListener.getAmbiguities()));
        }
        return parseTree;
    }

}
