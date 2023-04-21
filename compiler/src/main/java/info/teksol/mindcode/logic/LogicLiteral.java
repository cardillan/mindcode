package info.teksol.mindcode.logic;

public interface LogicLiteral extends LogicValue {

    default boolean isLiteral() {
        return true;
    }

    default boolean isFormattable() {
        return true;
    }

    String format();
}
