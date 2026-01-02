package info.teksol.mc.messages;

import info.teksol.mc.common.SourceElement;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.emulator.EmulatorMessage;
import info.teksol.mc.emulator.ExecutionFlag;
import info.teksol.mc.emulator.LInstruction;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public abstract class AbstractMessageEmitter implements MessageEmitter {
    protected final MessageConsumer messageConsumer;

    public AbstractMessageEmitter(MessageConsumer messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    public MessageConsumer messageConsumer() {
        return messageConsumer;
    }

    @Override
    public void addMessage(MindcodeMessage message) {
        messageConsumer.addMessage(message);
    }

    @Override
    public void error(SourceElement element, @PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.error(element.sourcePosition(), format, args));
    }

    @Override
    public void error(SourcePosition position, @PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.error(position, format, args));
    }

    @Override
    public void error(ExecutionFlag flag, int index, @Nullable LInstruction instruction, @PrintFormat String format, Object... args) {
        addMessage(EmulatorMessage.error(flag, index, instruction, format, args));
    }

    @Override
    public void error(@PrintFormat String format, Object... args) {
        addMessage(ToolMessage.error(format, args));
    }

    @Override
    public void warn(SourceElement element, @PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.warn(element.sourcePosition(), format, args));
    }

    @Override
    public void warn(SourcePosition position, @PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.warn(position, format, args));
    }

    @Override
    public void warn(ExecutionFlag flag, int index, @Nullable LInstruction instruction, @PrintFormat String format, Object... args) {
        addMessage(EmulatorMessage.warn(flag, index, instruction, format, args));
    }

    @Override
    public void warn(@PrintFormat String format, Object... args) {
        addMessage(ToolMessage.warn(format, args));
    }

    @Override
    public void info(@PrintFormat String format, Object... args) {
        addMessage(ToolMessage.info(format, args));
    }

    @Override
    public void timing(@PrintFormat String format, Object... args) {
        addMessage(TimingMessage.info(format, args));
    }

    @Override
    public void debug(String message) {
        addMessage(ToolMessage.debug(message));
    }

    @Override
    public void debug(@PrintFormat String format, Object... args) {
        addMessage(ToolMessage.debug(format, args));
    }
}
