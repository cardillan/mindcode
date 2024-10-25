package info.teksol.mindcode.logic;

public record LogicFunctionArgument(LogicValue value, boolean inModifier, boolean outModifier) {

    public boolean hasValue() {
        return value != null;
    }

    @Override
    public LogicValue value() {
        return value == null ? LogicNull.NULL : value;
    }

    public boolean hasInModifierOnly() {
        return inModifier && !outModifier;
    }
}
