package info.teksol.mc.messages;

import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;
import java.util.Objects;

@NullMarked
public record TimingMessage(MessageLevel level, String message) implements MindcodeMessage {

    public TimingMessage {
        Objects.requireNonNull(level);
        Objects.requireNonNull(message);
    }

    @Override
    public boolean isStable() {
        return false;
    }

    @Override
    public boolean console() {
        return true;
    }

    @Override
    public boolean logFile() {
        return false;
    }

    public static TimingMessage info(@PrintFormat String format, Object... args) {
        return new TimingMessage(MessageLevel.INFO, String.format(Locale.US, format, args));
    }

    public static TimingMessage debug(String message) {
        return new TimingMessage(MessageLevel.DEBUG, message);
    }
}
