package info.teksol.mindcode.compiler;

import org.intellij.lang.annotations.PrintFormat;

import java.util.Locale;
import java.util.Objects;

public record MindcodeMessage(MessageLevel level, String message) implements CompilerMessage {

    public MindcodeMessage {
        Objects.requireNonNull(level);
        Objects.requireNonNull(message);
    }

    public static MindcodeMessage error(String message) {
        return new MindcodeMessage(MessageLevel.ERROR, message);
    }

    public static MindcodeMessage warn(String message) {
        return new MindcodeMessage(MessageLevel.WARNING, message);
    }

    public static MindcodeMessage warn(@PrintFormat String format, Object... args) {
        return new MindcodeMessage(MessageLevel.WARNING, String.format(Locale.US, format, args));
    }

    public static MindcodeMessage info(String message) {
        return new MindcodeMessage(MessageLevel.INFO, message);
    }

    public static MindcodeMessage info(@PrintFormat String format, Object... args) {
        return new MindcodeMessage(MessageLevel.INFO, String.format(Locale.US, format, args));
    }

    public static MindcodeMessage debug(String message) {
        return new MindcodeMessage(MessageLevel.DEBUG, message);
    }

    public static MindcodeMessage debug(@PrintFormat String format, Object... args) {
        return new MindcodeMessage(MessageLevel.DEBUG, String.format(Locale.US, format, args));
    }


    @Override
    public String source() {
        return "Mindcode";
    }

    @Override
    public String toString() {
        return "MindcodeMessage{" +
                "level=" + level +
                ", message='" + message + '\'' +
                '}';
    }
}
