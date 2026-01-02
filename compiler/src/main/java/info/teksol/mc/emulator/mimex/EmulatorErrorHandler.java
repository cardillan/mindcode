package info.teksol.mc.emulator.mimex;

import info.teksol.mc.emulator.ExecutionFlag;
import info.teksol.mc.emulator.LInstruction;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.MessageConsumer;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;

@NullMarked
public class EmulatorErrorHandler extends AbstractMessageEmitter {
    private final Set<ExecutionFlag> flags;

    /// A runtime error was encountered
    protected boolean error;

    private final int traceLimit;
    private int traceCount = 0;

    private int index;
    private @Nullable LInstruction instruction;

    public EmulatorErrorHandler(MessageConsumer messageConsumer, Set<ExecutionFlag> flags, int traceLimit) {
        super(messageConsumer);
        this.flags = flags;
        this.traceLimit = traceLimit;
    }

    public boolean getFlag(ExecutionFlag flag) {
        return flags.contains(flag);
    }

    public boolean trace(ExecutionFlag flag) {
        return getFlag(flag) && traceCount++ < traceLimit;
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
