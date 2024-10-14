package info.teksol.mindcode.compiler;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MessageLevel;
import info.teksol.mindcode.MindcodeMessage;
import org.intellij.lang.annotations.PrintFormat;

import java.util.Locale;
import java.util.Objects;

public record MindcodeCompilerMessage(MessageLevel level, InputPosition inputPosition, String message) implements MindcodeMessage {

    public MindcodeCompilerMessage {
        Objects.requireNonNull(level);
        Objects.requireNonNull(inputPosition);
        Objects.requireNonNull(message);
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

    @Override
    public String toString() {
        return "MindcodeCompilerMessage{" +
                "level=" + level +
                ", inputPosition=" + inputPosition +
                ", message='" + message + '\'' +
                '}';
    }
}
