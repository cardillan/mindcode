package io.github.cardillan.mc.mindcode.logic.opcodes;

import io.github.cardillan.mc.mindcode.logic.arguments.LogicArgument;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicKeyword;
import io.github.cardillan.mc.mindcode.logic.arguments.Operation;
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

    public LogicArgument keyword() {
        return operation == null ? LogicKeyword.create(name) : operation;
    }
}
