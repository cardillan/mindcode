package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.evaluator.LogicReadable;
import org.jspecify.annotations.NullMarked;

/// Represents an argument to a logic instruction. Argument has the following properties:
///
/// - Argument type: type of the argument used. Some parameters require a specific type of argument, others allow
///   several types of argument.
/// - Mutability: describes the ways the value of the argument can change
/// - Mlog: mlog representation of the argument
/// - Value: if the argument represents a literal, value is its corresponding value. It can be a double, a text
///   or a specific object (e.g. `@coal`)
@NullMarked
public interface LogicArgument extends LogicReadable {
    /// @return type of the argument
    ArgumentType getType();

    ///  @return mutability of the argument
    ValueMutability getMutability();

    /// @return the mlog representation of the variable
    String toMlog();

    /// Determines whether two logic arguments are equal. The support for `UNSPECIFIED` types serves for testing code,
    /// where expected instructions are created using generic mlog arguments.
    static boolean isEqual(LogicArgument a1, LogicArgument a2) {
        return (a1.getType() == a2.getType()
                || a1.getType() == ArgumentType.UNSPECIFIED
                || a2.getType() == ArgumentType.UNSPECIFIED)
                && a1.toMlog().equals(a2.toMlog());
    }

    /// @return true when the argument is a compile-time constant
    default boolean isConstant() {
        return getMutability() == ValueMutability.CONSTANT;
    }

    /// @return true when the argument is a run-time constant (includes compile-time constants by definition)
    default boolean isImmutable() {
        return getMutability() == ValueMutability.CONSTANT || getMutability() == ValueMutability.IMMUTABLE;
    }

    /// @return true when the argument's value is volatile
    default boolean isVolatile() {
        return getMutability() == ValueMutability.VOLATILE;
    }

    /// @return true if this is a numeric literal.
    default boolean isNumericLiteral() {
        return false;
    }

    /// @return true if this is a writable variable
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

    /// @return true if this is a global variable (implies user-declared)
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

    /// @return true if this is an input function parameter
    default boolean isInput() {
        return false;
    }

    /// @return true if this is an output function parameter
    default boolean isOutput() {
        return false;
    }
}
