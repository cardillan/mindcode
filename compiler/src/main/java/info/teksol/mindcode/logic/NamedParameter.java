package info.teksol.mindcode.logic;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public record NamedParameter(InstructionParameterType type, String name, @Nullable Operation operation) {
    public NamedParameter {
        Objects.requireNonNull(type);
        Objects.requireNonNull(name);
    }

    public NamedParameter(InstructionParameterType type, String name) {
        this(type, name, null);
    }

    public Operation operation() {
        return Objects.requireNonNull(operation);
    }

    public LogicKeyword keyword() {
        return LogicKeyword.create(operation == null ? name : operation.toMlog());
    }
}
