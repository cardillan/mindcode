package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.messages.SourcePositionTranslator;
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

    @Override
    public MindcodeMessage translatePosition(SourcePositionTranslator translator) {
        if (!sourcePosition.isEmpty()) {
            SourcePosition translated = translator.apply(sourcePosition);
            if (translated != sourcePosition) {
                return new PositionalMessage(level, translated, message);
            }
        }
        return this;
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
