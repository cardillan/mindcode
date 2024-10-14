package info.teksol.mindcode;

import org.intellij.lang.annotations.PrintFormat;

import java.util.Locale;
import java.util.Objects;

public record ToolMessage(MessageLevel level, String message) implements MindcodeMessage {

    public ToolMessage {
        Objects.requireNonNull(level);
        Objects.requireNonNull(message);
    }

    public static ToolMessage error(String message) {
        return new ToolMessage(MessageLevel.ERROR, message);
    }

    public static ToolMessage error(@PrintFormat String format, Object... args) {
        return new ToolMessage(MessageLevel.ERROR, String.format(Locale.US, format, args));
    }

    public static ToolMessage warn(String message) {
        return new ToolMessage(MessageLevel.WARNING, message);
    }

    public static ToolMessage warn(@PrintFormat String format, Object... args) {
        return new ToolMessage(MessageLevel.WARNING, String.format(Locale.US, format, args));
    }

    public static ToolMessage info(String message) {
        return new ToolMessage(MessageLevel.INFO, message);
    }

    public static ToolMessage info(@PrintFormat String format, Object... args) {
        return new ToolMessage(MessageLevel.INFO, String.format(Locale.US, format, args));
    }

    public static ToolMessage debug(String message) {
        return new ToolMessage(MessageLevel.DEBUG, message);
    }

    public static ToolMessage debug(@PrintFormat String format, Object... args) {
        return new ToolMessage(MessageLevel.DEBUG, String.format(Locale.US, format, args));
    }

    @Override
    public String toString() {
        return "ToolMessage{" +
                "level=" + level +
                ", message='" + message + '\'' +
                '}';
    }
}
