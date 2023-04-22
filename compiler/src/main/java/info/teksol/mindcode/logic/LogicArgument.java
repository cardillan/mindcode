package info.teksol.mindcode.logic;

/**
 * Represents an argument to a logic instruction. Argument has the following properties:
 * <ul>
 * <li>Parameter type: type of the parameter the argument is passed into
 * <li>Argument type: type of the argument used. Some parameters require a specific type of argument, others allow
 * several types of argument.
 * <li>Literal: mlog representation of the argument
 * <li>Value: if the argument represents a literal, value is its corresponding value. It can be a double, a text
 * or a specific object (e.g. @coal)
 * </ul>
 *
 */
public interface LogicArgument {
    ArgumentType getType();

    String toMlog();

    static boolean isEqual(LogicArgument a1, LogicArgument a2) {
        return (a1.getType() == a2.getType()
                || a1.getType() == ArgumentType.UNSPECIFIED
                || a2.getType() == ArgumentType.UNSPECIFIED)
                && a1.toMlog().equals(a2.toMlog());
    }

    default boolean isLiteral() {
        return false;
    }

    default boolean isUserVariable() {
        return false;
    }

    default boolean isGlobalVariable() {
        return false;
    }

    default boolean isMainVariable() {
        return false;
    }

    default boolean isFunctionVariable() {
        return false;
    }

    default boolean isCompilerVariable() {
        return false;
    }

    default boolean isVolatile() {
        return false;
    }

    default double getDoubleValue() {
        return Double.NaN;
    }
}
