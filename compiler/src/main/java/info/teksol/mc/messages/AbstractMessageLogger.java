package info.teksol.mc.messages;

import info.teksol.mc.common.SourceElement;
import info.teksol.mc.common.SourcePosition;
import org.intellij.lang.annotations.PrintFormat;

public abstract class AbstractMessageLogger implements MessageLogger, MessageConsumer {

    public void error(SourceElement element, @PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.error(element.sourcePosition(), format, args));
    }

    public void error(SourcePosition position, @PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.error(position, format, args));
    }

    public void error(String text) {
        addMessage(ToolMessage.error(text));
    }

    public void error(@PrintFormat String format, Object... args) {
        addMessage(ToolMessage.error(format, args));
    }

    public void warn(SourceElement element, @PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.warn(element.sourcePosition(), format, args));
    }

    public void warn(SourcePosition position, @PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.warn(position, format, args));
    }

    public void info(String text) {
        addMessage(ToolMessage.info(text));
    }

    public void info(@PrintFormat String format, Object... args) {
        addMessage(ToolMessage.info(format, args));
    }

    public void timing(@PrintFormat String format, Object... args) {
        addMessage(TimingMessage.info(format, args));
    }

    public void debug(String message) {
        addMessage(ToolMessage.debug(message));
    }
}
