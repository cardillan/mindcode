package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.compiler.MindcodeCompilerMessage;
import org.intellij.lang.annotations.PrintFormat;

import java.util.function.Consumer;

public abstract class AbstractMessageEmitter implements MessageEmitter {
    protected final Consumer<MindcodeMessage> messageConsumer;

    public AbstractMessageEmitter(Consumer<MindcodeMessage> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    @Override
    public Consumer<MindcodeMessage> getMessageConsumer() {
        return messageConsumer;
    }

    @Override
    public void error(AstNode node, @PrintFormat String format, Object... args) {
        messageConsumer.accept(MindcodeCompilerMessage.error(node.getInputPosition(), format, args));
    }

    @Override
    public void error(InputPosition position, @PrintFormat String format, Object... args) {
        messageConsumer.accept(MindcodeCompilerMessage.error(position, format, args));
    }

    @Override
    public void warn(AstNode node, @PrintFormat String format, Object... args) {
        messageConsumer.accept(MindcodeCompilerMessage.warn(node.getInputPosition(), format, args));
    }

    @Override
    public void warn(InputPosition position, @PrintFormat String format, Object... args) {
        messageConsumer.accept(MindcodeCompilerMessage.warn(position, format, args));
    }
}
