package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;

import java.util.Objects;

public class LogicParameter extends LogicVariable {
    private final LogicValue value;

    private LogicParameter(SourcePosition position, String name, LogicValue value) {
        super(position, ArgumentType.PARAMETER, name);
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
