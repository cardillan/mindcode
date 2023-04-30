package info.teksol.mindcode.logic;

public interface LogicLiteral extends LogicValue {

    default boolean isLiteral() {
        return true;
    }

    String format();
}
