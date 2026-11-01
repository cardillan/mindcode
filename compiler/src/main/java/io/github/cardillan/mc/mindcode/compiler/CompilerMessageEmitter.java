package io.github.cardillan.mc.mindcode.compiler;

import io.github.cardillan.mc.common.SourceElement;
import io.github.cardillan.mc.common.SourcePosition;
import io.github.cardillan.mc.messages.AbstractMessageEmitter;
import io.github.cardillan.mc.messages.MessageConsumer;
import io.github.cardillan.mc.messages.MessageLevel;
import io.github.cardillan.mc.mindcode.compiler.optimization.CompilerMessage;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CompilerMessageEmitter extends AbstractMessageEmitter {
    public static final CompilerMessageEmitter DISCARD = new CompilerMessageEmitter(_ -> {});

    public CompilerMessageEmitter(MessageConsumer messageConsumer) {
        super(messageConsumer);
    }

    public void message(MessageLevel level, SourceElement node, @PrintFormat String format, Object... args) {
        addMessage(PositionalMessage.message(level, node.sourcePosition(), format, args));
    }

    public void error(SourceElement node, @PrintFormat String format, Object... args) {
        addMessage(PositionalMessage.error(node.sourcePosition(), format, args));
    }

    public void error(SourcePosition position, @PrintFormat String format, Object... args) {
        addMessage(PositionalMessage.error(position, format, args));
    }

    public void error(boolean warningOnly, SourceElement node, @PrintFormat String format, Object... args) {
        if (warningOnly) {
            warn(node, format, args);
        } else {
            error(node, format, args);
        }
    }

    public void strictError(SourceElement node, @PrintFormat String format, Object... args) {
        if (ContextFactory.getMessageContext().globalCompilerProfile().isStrict()) {
            error(node, format, args);
        } else {
            warn(node, format, args);
        }
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
