package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.common.SourceElement;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.optimization.CompilerMessage;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CompilerMessageEmitter extends AbstractMessageEmitter {

    public CompilerMessageEmitter(MessageConsumer messageConsumer) {
        super(messageConsumer);
    }

    public void error(SourceElement node, @PrintFormat String format, Object... args) {
        addMessage(PositionalMessage.error(node.sourcePosition(), format, args));
    }

    public void error(SourcePosition position, @PrintFormat String format, Object... args) {
        addMessage(PositionalMessage.error(position, format, args));
    }

    public void warn(SourceElement element, @PrintFormat String format, Object... args) {
        addMessage(PositionalMessage.warn(element.sourcePosition(), format, args));
    }

    public void warn(SourcePosition position, @PrintFormat String format, Object... args) {
        addMessage(PositionalMessage.warn(position, format, args));
    }

    public void error(@PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.error(format, args));
    }

    public void warn(@PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.warn(format, args));
    }

    public void info(@PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.info(format, args));
    }

    public void debug(@PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.debug(format, args));
    }

    public void debug(String message) {
        addMessage(CompilerMessage.debug(message));
    }
}
