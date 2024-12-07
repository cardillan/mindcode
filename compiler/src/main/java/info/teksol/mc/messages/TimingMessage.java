package info.teksol.mc.messages;

import org.intellij.lang.annotations.PrintFormat;

import java.util.Locale;
import java.util.Objects;

public record TimingMessage(MessageLevel level, String message) implements MindcodeMessage {

    public TimingMessage {
        Objects.requireNonNull(level);
        Objects.requireNonNull(message);
    }

    @Override
    public boolean isStable() {
        return false;
    }

    public static TimingMessage info(@PrintFormat String format, Object... args) {
        return new TimingMessage(MessageLevel.INFO, String.format(Locale.US, format, args));
    }

    public static TimingMessage debug(String message) {
        return new TimingMessage(MessageLevel.DEBUG, message);
    }

    @Override
    public String toString() {
        return "TimingMessage{" +
                "level=" + level +
                ", message='" + message + '\'' +
                '}';
    }
}
