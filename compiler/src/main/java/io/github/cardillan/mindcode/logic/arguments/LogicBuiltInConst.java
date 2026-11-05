package io.github.cardillan.mindcode.logic.arguments;

import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.mindcode.logic.instructions.InstructionProcessor;
import io.github.cardillan.mindcode.logic.mimex.LVariable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class LogicBuiltInConst extends LogicBuiltIn {
    private final double value;

    private LogicBuiltInConst(SourcePosition sourcePosition, String name, double value) {
        super(sourcePosition, name);
        this.value = value;
    }

    @Override
    public double getDoubleValue() {
        return value;
    }

    @Override
    public long getLongValue() {
        return (long) value;
    }

    public int getIntValue() {
        return (int) value;
    }

    @Override
    public boolean isObject() {
        return false;
    }

    public boolean isInteger() {
        return value == (int) value;
    }

    @Override
    public String format(@Nullable InstructionProcessor instructionProcessor) {
        return Objects.requireNonNull(instructionProcessor).formatNumber(value);
    }

    public static LogicBuiltIn create(InstructionProcessor processor, SourcePosition sourcePosition, String name) {
        LVariable var = processor.getMetadata().getLVar(name);
        return var != null && var.isNumericConstant()
                ? new LogicBuiltInConst(sourcePosition, name, var.numericValue())
                : LogicBuiltIn.create(processor, sourcePosition, name);
    }
}
