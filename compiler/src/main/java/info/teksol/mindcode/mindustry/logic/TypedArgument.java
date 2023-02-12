package info.teksol.mindcode.mindustry.logic;

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
}
