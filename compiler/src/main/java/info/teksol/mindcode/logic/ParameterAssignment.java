package info.teksol.mindcode.logic;

import java.util.Objects;

public record ParameterAssignment(LogicParameter parameter, LogicArgument argument) {
    public ParameterAssignment {
        Objects.requireNonNull(parameter);
        Objects.requireNonNull(argument);
    }

    public boolean isInput() {
        return parameter.isInput();
    }

    public boolean isOutput() {
        return parameter.isOutput();
    }

    public boolean isInputOrOutput() {
        return parameter.isInputOrOutput();
    }
}
