package info.teksol.mc.messages;

import info.teksol.mc.common.SourceElement;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.emulator.ExecutionFlag;
import info.teksol.mc.emulator.LInstruction;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface MessageEmitter {
    MessageConsumer messageConsumer();

    void addMessage(MindcodeMessage message);

    void error(SourceElement node, @PrintFormat String format, Object... args);

    void error(SourcePosition position, @PrintFormat String format, Object... args);

    void error(ExecutionFlag flag, int index, @Nullable LInstruction instruction, @PrintFormat String format, Object... args);

    void error(@PrintFormat String format, Object... args);

    void warn(SourceElement node, @PrintFormat String format, Object... args);

    void warn(SourcePosition position, @PrintFormat String format, Object... args);

    void warn(ExecutionFlag flag, int index, @Nullable LInstruction instruction, @PrintFormat String format, Object... args);

    void warn(@PrintFormat String format, Object... args);

    void info(@PrintFormat String format, Object... args);

    void timing(@PrintFormat String format, Object... args);

    void debug(String message);

    void debug(@PrintFormat String format, Object... args);
}
