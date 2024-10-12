package info.teksol.mindcode.compiler;

import info.teksol.mindcode.CompilerMessage;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MessageLevel;
import org.intellij.lang.annotations.PrintFormat;

import java.util.Locale;
import java.util.Objects;

public record MindcodeCompilerMessage(MessageLevel level, InputPosition inputPosition, String message) implements CompilerMessage {

    public MindcodeCompilerMessage {
        Objects.requireNonNull(level);
        Objects.requireNonNull(message);
    }

    @Deprecated
    public static MindcodeCompilerMessage error(@PrintFormat String format, Object... args) {
        return new MindcodeCompilerMessage(MessageLevel.ERROR, null, String.format(Locale.US, format, args));
    }

    public static MindcodeCompilerMessage error(InputPosition inputPosition, @PrintFormat String format, Object... args) {
        Objects.requireNonNull(inputPosition);
        return new MindcodeCompilerMessage(MessageLevel.ERROR, inputPosition, String.format(Locale.US, format, args));
    }

    @Deprecated
    public static MindcodeCompilerMessage warn(@PrintFormat String format, Object... args) {
        return new MindcodeCompilerMessage(MessageLevel.WARNING, null, String.format(Locale.US, format, args));
    }

    public static MindcodeCompilerMessage warn(InputPosition inputPosition, @PrintFormat String format, Object... args) {
        Objects.requireNonNull(inputPosition);
        return new MindcodeCompilerMessage(MessageLevel.WARNING, inputPosition, String.format(Locale.US, format, args));
    }

//    @Deprecated
//    public static MindcodeCompilerMessage info(@PrintFormat String format, Object... args) {
//        return new MindcodeCompilerMessage(MessageLevel.INFO, null, String.format(Locale.US, format, args));
//    }

    public static MindcodeCompilerMessage info(InputPosition inputPosition, @PrintFormat String format, Object... args) {
        Objects.requireNonNull(inputPosition);
        return new MindcodeCompilerMessage(MessageLevel.INFO, inputPosition, String.format(Locale.US, format, args));
    }

    @Deprecated
    public static MindcodeCompilerMessage debug(@PrintFormat String format, Object... args) {
        return new MindcodeCompilerMessage(MessageLevel.DEBUG, null, String.format(Locale.US, format, args));
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
