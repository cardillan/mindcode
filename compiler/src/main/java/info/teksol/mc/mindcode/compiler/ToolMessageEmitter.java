package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.messages.ToolMessage;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ToolMessageEmitter extends AbstractMessageEmitter {
    public ToolMessageEmitter(MessageConsumer messageConsumer) {
        super(messageConsumer);
    }

    public void error(@PrintFormat String format, Object... args) {
        addMessage(ToolMessage.warn(format, args));
    }

    public void warn(@PrintFormat String format, Object... args) {
        addMessage(ToolMessage.warn(format, args));
    }

    public void info(@PrintFormat String format, Object... args) {
        addMessage(ToolMessage.info(format, args));
    }

    public void info(String text) {
        addMessage(ToolMessage.info(text));
    }

    public void debug(String message) {
        addMessage(ToolMessage.debug(message));
    }

    public void debug(@PrintFormat String format, Object... args) {
        addMessage(ToolMessage.debug(format, args));
    }

}
