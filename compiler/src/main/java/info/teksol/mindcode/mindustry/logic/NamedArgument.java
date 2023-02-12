package info.teksol.mindcode.mindustry.logic;

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
    public String toString() {
        return "NamedArgument{" + "name=" + name + ", type=" + type + '}';
    }
}
