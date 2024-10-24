package info.teksol.mindcode.logic;

import java.util.Objects;

public class LogicParameter extends LogicVariable {
    private final LogicValue value;

    private LogicParameter(String name, LogicValue value) {
        super(ArgumentType.PARAMETER, name, false, false, false);
        this.value = Objects.requireNonNull(value);
    }

    public static LogicParameter parameter(String name, LogicValue value) {
        return new LogicParameter(name, value);
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
