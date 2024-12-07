package info.teksol.mc.messages;

import info.teksol.mc.common.SourcePosition;
import org.intellij.lang.annotations.PrintFormat;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

public record CompilerMessage(MessageLevel level, SourcePosition sourcePosition, String message) implements MindcodeMessage {

    public CompilerMessage {
        Objects.requireNonNull(level);
        Objects.requireNonNull(sourcePosition);
        Objects.requireNonNull(message);
    }

    @Override
    public MindcodeMessage translatePosition(SourcePositionTranslator translator) {
        if (!sourcePosition.isEmpty()) {
            SourcePosition translated = translator.apply(sourcePosition);
            if (translated != sourcePosition) {
                return new CompilerMessage(level, translated, message);
            }
        }
        return this;
    }

    public static CompilerMessage error(SourcePosition sourcePosition, @PrintFormat String format, Object... args) {
        Objects.requireNonNull(sourcePosition);
        return new CompilerMessage(MessageLevel.ERROR, sourcePosition, String.format(Locale.US, format, args));
    }

    public static CompilerMessage warn(SourcePosition sourcePosition, @PrintFormat String format, Object... args) {
        Objects.requireNonNull(sourcePosition);
        return new CompilerMessage(MessageLevel.WARNING, sourcePosition, String.format(Locale.US, format, args));
    }

    public static CompilerMessage info(SourcePosition sourcePosition, @PrintFormat String format, Object... args) {
        Objects.requireNonNull(sourcePosition);
        return new CompilerMessage(MessageLevel.INFO, sourcePosition, String.format(Locale.US, format, args));
    }

    public static CompilerMessage debug(SourcePosition sourcePosition, @PrintFormat String format, Object... args) {
        Objects.requireNonNull(sourcePosition);
        return new CompilerMessage(MessageLevel.DEBUG, sourcePosition, String.format(Locale.US, format, args));
    }

    public String formatMessage(Function<SourcePosition, String> positionFormatter) {
        return positionFormatter.apply(sourcePosition()) + " " + (isErrorOrWarning() ? level().getTitle() + ": " : "") + formatMessage();
    }

    @Override
    public String toString() {
        return "MindcodeCompilerMessage{" +
                "level=" + level +
                ", sourcePosition=" + sourcePosition +
                ", message='" + message + '\'' +
                '}';
    }
}
