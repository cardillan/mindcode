package info.teksol.mindcode.logic;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.ast.FunctionArgument;

import java.util.Objects;

public record LogicFunctionArgument(InputPosition pos, LogicValue value, boolean inModifier, boolean outModifier) {

    public LogicFunctionArgument(FunctionArgument argument, LogicValue value) {
        this(argument.inputPosition(), Objects.requireNonNull(value), argument.hasInModifier(), argument.hasOutModifier());
    }

    public LogicFunctionArgument(FunctionArgument argument) {
        this(argument.inputPosition(), null, argument.hasInModifier(), argument.hasOutModifier());
    }

    public boolean hasValue() {
        return value != null;
    }

    @Override
    public LogicValue value() {
        return value == null ? LogicNull.NULL : value;
    }

    public boolean hasModifier() {
        return inModifier || outModifier;
    }

    public boolean hasInModifierOnly() {
        return inModifier && !outModifier;
    }
}
