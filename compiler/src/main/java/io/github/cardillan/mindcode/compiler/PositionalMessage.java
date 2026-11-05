package io.github.cardillan.mindcode.compiler;

import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.mindcode.messages.MessageLevel;
import io.github.cardillan.mindcode.messages.MindcodeMessage;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;
import java.util.Objects;

@NullMarked
public record PositionalMessage(MessageLevel level, SourcePosition sourcePosition, String message) implements MindcodeMessage {

    public PositionalMessage {
        Objects.requireNonNull(level);
        Objects.requireNonNull(sourcePosition);
        Objects.requireNonNull(message);
    }

    public static PositionalMessage message(MessageLevel level, SourcePosition sourcePosition, @PrintFormat String format, Object... args) {
        Objects.requireNonNull(sourcePosition);
        return new PositionalMessage(level, sourcePosition, String.format(Locale.US, format, args));
    }

    public static PositionalMessage error(SourcePosition sourcePosition, @PrintFormat String format, Object... args) {
        Objects.requireNonNull(sourcePosition);
        return new PositionalMessage(MessageLevel.ERROR, sourcePosition, String.format(Locale.US, format, args));
    }

    public static PositionalMessage warn(SourcePosition sourcePosition, @PrintFormat String format, Object... args) {
        Objects.requireNonNull(sourcePosition);
        return new PositionalMessage(MessageLevel.WARNING, sourcePosition, String.format(Locale.US, format, args));
    }

    public static PositionalMessage info(SourcePosition sourcePosition, @PrintFormat String format, Object... args) {
        Objects.requireNonNull(sourcePosition);
        return new PositionalMessage(MessageLevel.INFO, sourcePosition, String.format(Locale.US, format, args));
    }

    public static PositionalMessage debug(SourcePosition sourcePosition, @PrintFormat String format, Object... args) {
        Objects.requireNonNull(sourcePosition);
        return new PositionalMessage(MessageLevel.DEBUG, sourcePosition, String.format(Locale.US, format, args));
    }
}
