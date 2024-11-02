package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.AstElement;
import info.teksol.mindcode.CompilerMessage;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeMessage;
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
    public void addMessage(MindcodeMessage message) {
        messageConsumer.accept(message);
    }

    @Override
    public void error(AstElement element, @PrintFormat String format, Object... args) {
        messageConsumer.accept(CompilerMessage.error(element.inputPosition(), format, args));
    }

    @Override
    public void error(InputPosition position, @PrintFormat String format, Object... args) {
        messageConsumer.accept(CompilerMessage.error(position, format, args));
    }

    @Override
    public void warn(AstElement element, @PrintFormat String format, Object... args) {
        messageConsumer.accept(CompilerMessage.warn(element.inputPosition(), format, args));
    }

    @Override
    public void warn(InputPosition position, @PrintFormat String format, Object... args) {
        messageConsumer.accept(CompilerMessage.warn(position, format, args));
    }
}
