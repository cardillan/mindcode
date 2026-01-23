package info.teksol.mc.emulator.mimex;

import info.teksol.mc.emulator.EmulatorMessageEmitter;
import info.teksol.mc.emulator.ExecutionFlag;
import info.teksol.mc.emulator.LInstruction;
import info.teksol.mc.messages.MessageConsumer;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;

import static info.teksol.mc.emulator.ExecutionFlag.DUMP_VARIABLES_ON_STOP;
import static info.teksol.mc.emulator.ExecutionFlag.TRACE_EXECUTION;

@NullMarked
public class EmulatorMessageHandler extends EmulatorMessageEmitter {
    private final Set<ExecutionFlag> flags;

    /// A runtime error was encountered
    protected boolean error;

    private final int traceLimit;
    private int traceCount = 0;

    private int index;
    private @Nullable LInstruction instruction;

    public EmulatorMessageHandler(MessageConsumer messageConsumer, Set<ExecutionFlag> flags, int traceLimit) {
        super(messageConsumer);
        this.flags = flags;
        this.traceLimit = traceLimit;
    }

    public boolean getFlag(ExecutionFlag flag) {
        return flags.contains(flag);
    }

    public void trace(ExecutionFlag flag, @PrintFormat String format, Object... args) {
        if (getFlag(flag) && traceCount++ < traceLimit) {
            debug(-1, null, format, args);
        }
    }

    public void trace(@PrintFormat String format, Object... args) {
        trace(TRACE_EXECUTION, format, args);
    }

    public void dump(@PrintFormat String format, Object... args) {
        if (getFlag(DUMP_VARIABLES_ON_STOP)) {
            debug(format, args);
        }
    }

    public void beginExecution(int index, LInstruction instruction) {
        this.index = index;
        this.instruction = instruction;
    }

    public boolean error(ExecutionFlag flag, @PrintFormat String message, Object... args) {
        if (!error && getFlag(flag)) {
            error(flag, index, instruction, message, args);
            error = true;
            return true;
        } else {
            return false;
        }
    }

    public boolean isError() {
        return error;
    }
}
