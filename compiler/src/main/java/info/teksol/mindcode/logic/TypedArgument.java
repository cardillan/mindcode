package info.teksol.mindcode.logic;

import java.util.Objects;

public class TypedArgument {
    private final ArgumentType argumentType;
    private final String value;

    public TypedArgument(ArgumentType argumentType, String value) {
        this.argumentType = argumentType;
        this.value = value;
    }

    public ArgumentType getArgumentType() {
        return argumentType;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(argumentType, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final TypedArgument other = (TypedArgument) obj;
        return this.argumentType == other.argumentType
                && Objects.equals(this.value, other.value);
    }

    @Override
    public String toString() {
        return "NamedArgument{" + "type=" + argumentType + ", value=\"" + value + "\"}";
    }
}
