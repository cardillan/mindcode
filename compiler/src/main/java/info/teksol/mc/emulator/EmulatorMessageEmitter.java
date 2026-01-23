package info.teksol.mc.emulator;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.messages.MessageLevel;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

public class EmulatorMessageEmitter extends AbstractMessageEmitter {

    public EmulatorMessageEmitter(MessageConsumer messageConsumer) {
        super(messageConsumer);
    }

    public void error(ExecutionFlag flag, int index, @Nullable LInstruction instruction, @PrintFormat String format, Object... args) {
        addMessage(EmulatorMessage.error(flag, index, instruction, format, args));
    }

    public void warn(ExecutionFlag flag, int index, @Nullable LInstruction instruction, @PrintFormat String format, Object... args) {
        addMessage(EmulatorMessage.warn(flag, index, instruction, format, args));
    }

    public void warn(@PrintFormat String format, Object... args) {
        addMessage(EmulatorMessage.warn(format, args));
    }

    public void info(int index, @Nullable LInstruction instruction, @PrintFormat String format, Object... args) {
        addMessage(EmulatorMessage.info(index, instruction, format, args));
    }

    public void info(@PrintFormat String format, Object... args) {
        addMessage(EmulatorMessage.info(format, args));
    }

    public void info(String message) {
        addMessage(EmulatorMessage.info(message));
    }

    public void debug(int index, @Nullable LInstruction instruction, @PrintFormat String format, Object... args) {
        addMessage(EmulatorMessage.debug(index, instruction, format, args));
    }

    public void debug(@PrintFormat String format, Object... args) {
        addMessage(new EmulatorMessage(MessageLevel.DEBUG, String.format(Locale.US, format, args)));
    }

    public void debug(String message) {
        addMessage(EmulatorMessage.debug(message));
    }
}
