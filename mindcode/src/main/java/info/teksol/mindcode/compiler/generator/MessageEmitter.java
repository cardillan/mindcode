package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.compiler.MindcodeCompilerMessage;
import org.intellij.lang.annotations.PrintFormat;

import java.util.function.Consumer;

public abstract class MessageEmitter {
    protected final Consumer<MindcodeMessage> messageConsumer;

    public MessageEmitter(Consumer<MindcodeMessage> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    protected void error(AstNode node, @PrintFormat String format, Object... args) {
        messageConsumer.accept(MindcodeCompilerMessage.error(node.getInputPosition(), format, args));
    }

    protected void error(InputPosition position, @PrintFormat String format, Object... args) {
        messageConsumer.accept(MindcodeCompilerMessage.error(position, format, args));
    }
}
