package info.teksol.mindcode.logic;

public interface LogicValue extends LogicArgument {
    /**
     * Indicates that an expressions using the value can be numerically evaluated at compile time.
     * All literals can be evaluated. Built-in constants can only be evaluated if they aren't a variable
     * (e.g. @coal can be evaluated, but @thisx cannot).
     *
     * @return true if the expression can be evaluated.
     */
    boolean canEvaluate();

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
