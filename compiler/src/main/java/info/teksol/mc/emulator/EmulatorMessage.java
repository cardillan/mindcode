package info.teksol.mc.emulator;

import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.messages.MindcodeMessage;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

@NullMarked
public record EmulatorMessage(
        MessageLevel level,
        @Nullable ExecutionFlag flag,
        int index,
        @Nullable LInstruction instruction,
        String text) implements MindcodeMessage {

    public EmulatorMessage {
        Objects.requireNonNull(level);
        Objects.requireNonNull(text);
    }

    @Override
    public String message() {
        if (level.weakerOrEqual(MessageLevel.INFO)) {
            return text;
        } else {
            String prefix = instruction == null ? "Runtime error occurred:\n" : String.format("Runtime error at instruction #%d: '%s':\n", index, instruction);
            String suffix = flag == null || !flag.isSettable() ? "" : String.format("\nUse the '#set %s = false;' directive to ignore this error.", flag.getOptionName());
            return prefix + text + suffix;
        }
    }

    public static EmulatorMessage error(ExecutionFlag flag, int index, @Nullable LInstruction instruction, @PrintFormat String format, Object... args) {
        return new EmulatorMessage(MessageLevel.ERROR, flag, index, instruction, String.format(Locale.US, format, args));
    }

    public static EmulatorMessage warn(ExecutionFlag flag, int index, @Nullable LInstruction instruction, @PrintFormat String format, Object... args) {
        return new EmulatorMessage(MessageLevel.WARNING, flag, index, instruction, String.format(Locale.US, format, args));
    }

    public static EmulatorMessage info(int index, @Nullable LInstruction instruction, @PrintFormat String format, Object... args) {
        return new EmulatorMessage(MessageLevel.INFO, null, index, instruction, String.format(Locale.US, format, args));
    }

    public static EmulatorMessage info(@PrintFormat String format, Object... args) {
        return new EmulatorMessage(MessageLevel.INFO, null, -1, null, String.format(Locale.US, format, args));
    }

    public static EmulatorMessage debug(int index, @Nullable LInstruction instruction, @PrintFormat String format, Object... args) {
        return new EmulatorMessage(MessageLevel.DEBUG, null, index, instruction, String.format(Locale.US, format, args));
    }
}
