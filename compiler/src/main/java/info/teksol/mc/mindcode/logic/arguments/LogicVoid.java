package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public enum LogicVoid implements LogicValue {
    VOID;

    @Override
    public SourcePosition sourcePosition() {
        return SourcePosition.EMPTY;
    }

    @Override
    public ArgumentType getType() {
        return ArgumentType.NULL_LITERAL;
    }

    @Override
    public ValueMutability getMutability() {
        return ValueMutability.CONSTANT;
    }

    @Override
    public boolean isLvalue() {
        return false;
    }

    @Override
    public boolean isNumericLiteral() {
        return true;
    }

    @Override
    public String toMlog() {
        return "null";
    }

    @Override
    public String format(@Nullable InstructionProcessor instructionProcessor) {
        return "null";
    }

    @Override
    public double getDoubleValue() {
        return 0.0;
    }

    @Override
    public long getLongValue() {
        return 0;
    }

    @Override
    public @Nullable Object getObject() {
        return null;
    }

    @Override
    public boolean isObject() {
        return true;
    }
}
