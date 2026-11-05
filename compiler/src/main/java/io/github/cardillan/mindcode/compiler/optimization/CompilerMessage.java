package io.github.cardillan.mindcode.compiler.optimization;

import io.github.cardillan.mindcode.messages.MessageLevel;
import io.github.cardillan.mindcode.messages.MindcodeMessage;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;
import java.util.Objects;

@NullMarked
public record CompilerMessage(MessageLevel level, String message) implements MindcodeMessage {

    public CompilerMessage {
        Objects.requireNonNull(level);
        Objects.requireNonNull(message);
    }

    public static CompilerMessage error(@PrintFormat String format, Object... args) {
        return new CompilerMessage(MessageLevel.ERROR, String.format(Locale.US, format, args));
    }

    public static CompilerMessage warn(@PrintFormat String format, Object... args) {
        return new CompilerMessage(MessageLevel.WARNING, String.format(Locale.US, format, args));
    }

    public static CompilerMessage info(@PrintFormat String format, Object... args) {
        return new CompilerMessage(MessageLevel.INFO, String.format(Locale.US, format, args));
    }

    public static CompilerMessage debug(String message) {
        return new CompilerMessage(MessageLevel.DEBUG, message);
    }

    public static CompilerMessage debug(@PrintFormat String format, Object... args) {
        return new CompilerMessage(MessageLevel.DEBUG, String.format(Locale.US, format, args));
    }
}
