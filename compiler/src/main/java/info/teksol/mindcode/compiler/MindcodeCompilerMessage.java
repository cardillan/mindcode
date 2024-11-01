package info.teksol.mindcode.compiler;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.InputPositionTranslator;
import info.teksol.mindcode.MessageLevel;
import info.teksol.mindcode.MindcodeMessage;
import org.intellij.lang.annotations.PrintFormat;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

public record MindcodeCompilerMessage(MessageLevel level, InputPosition inputPosition, String message) implements MindcodeMessage {

    public MindcodeCompilerMessage {
        Objects.requireNonNull(level);
        Objects.requireNonNull(inputPosition);
        Objects.requireNonNull(message);
    }

    @Override
    public MindcodeMessage translatePosition(InputPositionTranslator translator) {
        if (!inputPosition.isEmpty()) {
            InputPosition translated = translator.apply(inputPosition);
            if (translated != inputPosition) {
                return new MindcodeCompilerMessage(level, translated, message);
            }
        }
        return this;
    }

    public static MindcodeCompilerMessage error(InputPosition inputPosition, @PrintFormat String format, Object... args) {
        Objects.requireNonNull(inputPosition);
        return new MindcodeCompilerMessage(MessageLevel.ERROR, inputPosition, String.format(Locale.US, format, args));
    }

    public static MindcodeCompilerMessage warn(InputPosition inputPosition, @PrintFormat String format, Object... args) {
        Objects.requireNonNull(inputPosition);
        return new MindcodeCompilerMessage(MessageLevel.WARNING, inputPosition, String.format(Locale.US, format, args));
    }

    public static MindcodeCompilerMessage info(InputPosition inputPosition, @PrintFormat String format, Object... args) {
        Objects.requireNonNull(inputPosition);
        return new MindcodeCompilerMessage(MessageLevel.INFO, inputPosition, String.format(Locale.US, format, args));
    }

    public static MindcodeCompilerMessage debug(InputPosition inputPosition, @PrintFormat String format, Object... args) {
        Objects.requireNonNull(inputPosition);
        return new MindcodeCompilerMessage(MessageLevel.DEBUG, inputPosition, String.format(Locale.US, format, args));
    }

    public String formatMessage(Function<InputPosition, String> positionFormatter) {
        return positionFormatter.apply(inputPosition()) + " " + (isErrorOrWarning() ? level().getTitle() + ": " : "") + formatMessage();
    }

    @Override
    public String toString() {
        return "MindcodeCompilerMessage{" +
                "level=" + level +
                ", inputPosition=" + inputPosition +
                ", message='" + message + '\'' +
                '}';
    }
}
