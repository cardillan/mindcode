package info.teksol.mindcode.logic;

import java.util.Objects;

public record TypedArgument(InstructionParameterType type, LogicArgument argument) {
    public TypedArgument {
        Objects.requireNonNull(type);
        Objects.requireNonNull(argument);
    }

    public boolean isInput() {
        return type.isInput();
    }

    public boolean isOutput() {
        return type.isOutput();
    }

    public boolean isInputOrOutput() {
        return type.isInputOrOutput();
    }
}
