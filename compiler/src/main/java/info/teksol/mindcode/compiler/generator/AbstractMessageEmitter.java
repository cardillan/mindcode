package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.*;
import info.teksol.mindcode.v3.MessageConsumer;
import org.intellij.lang.annotations.PrintFormat;

public abstract class AbstractMessageEmitter implements MessageEmitter {
    protected final MessageConsumer messageConsumer;

    public AbstractMessageEmitter(MessageConsumer messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    public MessageConsumer messageConsumer() {
        return messageConsumer;
    }

    @Override
    public void addMessage(MindcodeMessage message) {
        messageConsumer.addMessage(message);
    }

    @Override
    public void error(AstElement element, @PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.error(element.inputPosition(), format, args));
    }

    @Override
    public void error(InputPosition position, @PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.error(position, format, args));
    }

    @Override
    public void error(@PrintFormat String format, Object... args) {
        addMessage(ToolMessage.error(format, args));
    }

    @Override
    public void warn(AstElement element, @PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.warn(element.inputPosition(), format, args));
    }

    @Override
    public void warn(InputPosition position, @PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.warn(position, format, args));
    }

    @Override
    public void info(@PrintFormat String format, Object... args) {
        addMessage(ToolMessage.info(format, args));
    }

    @Override
    public void timing(@PrintFormat String format, Object... args) {
        addMessage(TimingMessage.info(format, args));
    }

    @Override
    public void debug(String message) {
        addMessage(ToolMessage.debug(message));
    }
}
