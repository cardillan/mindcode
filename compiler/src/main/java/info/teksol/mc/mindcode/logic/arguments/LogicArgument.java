package info.teksol.mc.mindcode.logic.arguments;

/// Represents an argument to a logic instruction. Argument has the following properties:
///
/// - Parameter type: type of the parameter the argument is passed into
/// - Argument type: type of the argument used. Some parameters require a specific type of argument, others allow
///   several types of argument.
/// - Literal: mlog representation of the argument
/// - Value: if the argument represents a literal, value is its corresponding value. It can be a double, a text
///   or a specific object (e.g. @coal)
public interface LogicArgument {
    /// @return type of the argument
    ArgumentType getType();

    /// @return the mlog representation of the variable
    String toMlog();

    static boolean isEqual(LogicArgument a1, LogicArgument a2) {
        return (a1.getType() == a2.getType()
                || a1.getType() == ArgumentType.UNSPECIFIED
                || a2.getType() == ArgumentType.UNSPECIFIED)
                && a1.toMlog().equals(a2.toMlog());
    }

    // TODO Create the following boolean properties
    //      - isCompileConstant: can be evaluated at compile time (replaces LogicValue.canEvaluate)
    //      - isRuntimeConstant: doesn't change value at runtime, e.g. @thisx or a parameter (probably replaces LogicValue.isConstant)
    //      - isVolatile: can change value outside the program, e.g. @time, @unit
    //      Review all other flags ad methods and try to get rid of them

    /// @return true if this is a numeric literal.
    default boolean isNumericLiteral() {
        return false;
    }

    /// @return true if this is an input function parameter
    default boolean isInput() {
        return false;
    }

    /// @return true if this is an output function parameter
    default boolean isOutput() {
        return false;
    }

    /// @return true if this is writable variable
    default boolean isWritable() {
        return false;
    }

    /// @return true if this is a user-declared variable
    default boolean isUserVariable() {
        return false;
    }

    /// @return true if this is a writable user-declared variable
    default boolean isUserWritable() {
        return false;
    }

    /** @return true if this is a global variable (implies user-declared) */
    default boolean isGlobalVariable() {
        return false;
    }

    /// @return true if this is a main variable (implies user-declared)
    default boolean isMainVariable() {
        return false;
    }

    /// @return true if this is a function parameter or variable (implies user-declared)
    default boolean isLocalVariable() {
        return false;
    }

    /// @return true if this is a temporary variable
    default boolean isTemporaryVariable() {
        return false;
    }

    /// @return true if this is a special compiler-generated variable
    default boolean isCompilerVariable() {
        return false;
    }

    /// @return true if this is argument is volatile -- its value may change independently of the program
    default boolean isVolatile() {
        return false;
    }
}
