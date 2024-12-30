package info.teksol.mc.messages;

import info.teksol.mc.common.AstElement;
import info.teksol.mc.common.InputPosition;
import org.intellij.lang.annotations.PrintFormat;

public abstract class AbstractMessageLogger implements MessageLogger, MessageConsumer {

    public void error(AstElement element, @PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.error(element.inputPosition(), format, args));
    }

    public void error(InputPosition position, @PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.error(position, format, args));
    }

    public void error(String text) {
        addMessage(ToolMessage.error(text));
    }

    public void error(@PrintFormat String format, Object... args) {
        addMessage(ToolMessage.error(format, args));
    }

    public void warn(AstElement element, @PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.warn(element.inputPosition(), format, args));
    }

    public void warn(InputPosition position, @PrintFormat String format, Object... args) {
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
