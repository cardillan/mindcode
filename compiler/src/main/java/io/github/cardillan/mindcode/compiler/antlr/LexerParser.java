package io.github.cardillan.mindcode.compiler.antlr;

import io.github.cardillan.mindcode.common.InputFile;
import io.github.cardillan.mindcode.compiler.MindcodeErrorListener;
import io.github.cardillan.mindcode.compiler.MindcodeErrorStrategy;
import io.github.cardillan.mindcode.compiler.antlr.MindcodeParser.AstModuleContext;
import io.github.cardillan.mindcode.messages.ERR;
import io.github.cardillan.mindcode.messages.MessageConsumer;
import io.github.cardillan.mindcode.messages.ToolMessage;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LexerParser {

    public static CommonTokenStream createTokenStream(MessageConsumer messageConsumer, InputFile inputFile) {
        MindcodeErrorListener errorListener = new MindcodeErrorListener(messageConsumer, inputFile);
        // We're adding a missing newline at the end because it makes some grammar definitions way easier
        int len = inputFile.getCode().length();
        char last = len > 0 ? inputFile.getCode().charAt(len - 1) : 0;
        MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(
                last == '\n' || last == '\r' ? inputFile.getCode() : inputFile.getCode() + "\n"));
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
