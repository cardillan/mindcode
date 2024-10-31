package info.teksol.mindcode.logic;

public interface LogicValue extends LogicArgument {

    /** @return true if the value is a compile-time constant */
    boolean isConstant();

    /**
     * Provides a text representation of the contained value as if printed by Mindustry Logic
     * Supported only for compile-time constants. Is not the same as an mlog representation!
     *
     * @return a text representation of the contained value
     */
    String format();
}
