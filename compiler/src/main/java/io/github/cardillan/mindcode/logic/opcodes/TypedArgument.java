package io.github.cardillan.mindcode.logic.opcodes;

import io.github.cardillan.mindcode.logic.arguments.LogicArgument;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public record TypedArgument(InstructionParameterType type, LogicArgument argument) {
    public TypedArgument {
        Objects.requireNonNull(type);
        Objects.requireNonNull(argument);
    }

    public boolean isInput() {
        return type.isInput();
    }

    public boolean isInputOnly() {
        return type.isInputOnly();
    }

    public boolean isOutput() {
        return type.isOutput();
    }

    public boolean isInputOrOutput() {
        return type.isInputOrOutput();
    }
}
