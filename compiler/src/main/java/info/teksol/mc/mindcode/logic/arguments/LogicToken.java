package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class LogicToken implements LogicValue {
    private final String mlog;

    public LogicToken(String mlog) {
        this.mlog = mlog;
    }

    @Override
    public String format(@Nullable InstructionProcessor instructionProcessor) {
        return "";
    }

    @Override
    public boolean isLvalue() {
        return false;
    }

    @Override
    public SourcePosition sourcePosition() {
        return SourcePosition.EMPTY;
    }

    @Override
    public ArgumentType getType() {
        return ArgumentType.UNSPECIFIED;
    }

    @Override
    public ValueMutability getMutability() {
        return ValueMutability.VOLATILE;
    }

    @Override
    public String toMlog() {
        return mlog;
    }
}
