package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.MessageLevel;
import info.teksol.mindcode.MindcodeMessage;
import org.intellij.lang.annotations.PrintFormat;

import java.util.Locale;
import java.util.Objects;

public record OptimizerMessage(MessageLevel level, String message) implements MindcodeMessage {

    public OptimizerMessage {
        Objects.requireNonNull(level);
        Objects.requireNonNull(message);
    }

    public static OptimizerMessage error(@PrintFormat String format, Object... args) {
        return new OptimizerMessage(MessageLevel.ERROR, String.format(Locale.US, format, args));
    }

    public static OptimizerMessage warn(@PrintFormat String format, Object... args) {
        return new OptimizerMessage(MessageLevel.WARNING, String.format(Locale.US, format, args));
    }

    public static OptimizerMessage info(@PrintFormat String format, Object... args) {
        return new OptimizerMessage(MessageLevel.INFO, String.format(Locale.US, format, args));
    }

    public static OptimizerMessage debug(String message) {
        return new OptimizerMessage(MessageLevel.DEBUG, message);
    }

    public static OptimizerMessage debug(@PrintFormat String format, Object... args) {
        return new OptimizerMessage(MessageLevel.DEBUG, String.format(Locale.US, format, args));
    }

    @Override
    public String toString() {
        return "MindcodeOptimizerMessage{" +
                "level=" + level +
                ", message='" + message + '\'' +
                '}';
    }
}
