package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.MessageLevel;
import info.teksol.mindcode.MindcodeMessage;
import org.intellij.lang.annotations.PrintFormat;

import java.util.Locale;
import java.util.Objects;

public record MindcodeOptimizerMessage(MessageLevel level, String message) implements MindcodeMessage {

    public MindcodeOptimizerMessage {
        Objects.requireNonNull(level);
        Objects.requireNonNull(message);
    }

    public static MindcodeOptimizerMessage error(@PrintFormat String format, Object... args) {
        return new MindcodeOptimizerMessage(MessageLevel.ERROR, String.format(Locale.US, format, args));
    }

    public static MindcodeOptimizerMessage warn(@PrintFormat String format, Object... args) {
        return new MindcodeOptimizerMessage(MessageLevel.WARNING, String.format(Locale.US, format, args));
    }

    public static MindcodeOptimizerMessage info(@PrintFormat String format, Object... args) {
        return new MindcodeOptimizerMessage(MessageLevel.INFO, String.format(Locale.US, format, args));
    }

    public static MindcodeOptimizerMessage debug(String message) {
        return new MindcodeOptimizerMessage(MessageLevel.DEBUG, message);
    }

    public static MindcodeOptimizerMessage debug(@PrintFormat String format, Object... args) {
        return new MindcodeOptimizerMessage(MessageLevel.DEBUG, String.format(Locale.US, format, args));
    }

    @Override
    public String toString() {
        return "MindcodeOptimizerMessage{" +
                "level=" + level +
                ", message='" + message + '\'' +
                '}';
    }
}
