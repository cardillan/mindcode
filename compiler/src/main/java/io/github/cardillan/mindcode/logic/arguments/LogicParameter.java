package io.github.cardillan.mindcode.logic.arguments;

import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.mindcode.compiler.ast.nodes.AstIdentifier;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class LogicParameter extends LogicVariable {
    private final LogicValue value;

    private LogicParameter(SourcePosition position, String name, LogicValue value) {
        super(position, ArgumentType.PROGRAM_PARAMETER, ValueMutability.IMMUTABLE, name, name, null);
        this.value = Objects.requireNonNull(value);
    }

    public static LogicParameter parameter(AstIdentifier identifier, LogicValue value) {
        return new LogicParameter(identifier.sourcePosition(), identifier.getName(), value);
    }

    public LogicValue getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "LogicParameter{" +
                "argumentType=" + argumentType +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
