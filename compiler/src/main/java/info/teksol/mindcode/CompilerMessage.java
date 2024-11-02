package info.teksol.mindcode;

import org.intellij.lang.annotations.PrintFormat;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

public record CompilerMessage(MessageLevel level, InputPosition inputPosition, String message) implements MindcodeMessage {

    public CompilerMessage {
        Objects.requireNonNull(level);
        Objects.requireNonNull(inputPosition);
        Objects.requireNonNull(message);
    }

    @Override
    public MindcodeMessage translatePosition(InputPositionTranslator translator) {
        if (!inputPosition.isEmpty()) {
            InputPosition translated = translator.apply(inputPosition);
            if (translated != inputPosition) {
                return new CompilerMessage(level, translated, message);
            }
        }
        return this;
    }

    public static CompilerMessage error(InputPosition inputPosition, @PrintFormat String format, Object... args) {
        Objects.requireNonNull(inputPosition);
        return new CompilerMessage(MessageLevel.ERROR, inputPosition, String.format(Locale.US, format, args));
    }

    public static CompilerMessage warn(InputPosition inputPosition, @PrintFormat String format, Object... args) {
        Objects.requireNonNull(inputPosition);
        return new CompilerMessage(MessageLevel.WARNING, inputPosition, String.format(Locale.US, format, args));
    }

    public static CompilerMessage info(InputPosition inputPosition, @PrintFormat String format, Object... args) {
        Objects.requireNonNull(inputPosition);
        return new CompilerMessage(MessageLevel.INFO, inputPosition, String.format(Locale.US, format, args));
    }

    public static CompilerMessage debug(InputPosition inputPosition, @PrintFormat String format, Object... args) {
        Objects.requireNonNull(inputPosition);
        return new CompilerMessage(MessageLevel.DEBUG, inputPosition, String.format(Locale.US, format, args));
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
