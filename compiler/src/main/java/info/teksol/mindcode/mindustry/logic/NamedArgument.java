package info.teksol.mindcode.mindustry.logic;

import java.util.Objects;

public class NamedArgument {
    private final String name;
    private final ArgumentType type;

    public NamedArgument(ArgumentType type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArgumentType getType() {
        return type;
    }


    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final NamedArgument other = (NamedArgument) obj;
        return this.type == other.type
                && Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return "NamedArgument{" + "name=\"" + name + "\", type=" + type + '}';
    }
}
