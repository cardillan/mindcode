package info.teksol.mc.evaluator;

import info.teksol.mc.emulator.MlogReadable;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface LogicReadable extends MlogReadable {
    /// Indicates that an expressions using the value can be evaluated at compile time.
    /// All literals can be evaluated. Built-in constants can only be evaluated if they aren't a variable
    /// (e.g. @coal can be evaluated, but @thisx cannot).
    ///
    /// @return true if the expression can be evaluated.
    boolean isConstant();

    default boolean isNumericConstant() {
        return isConstant() && !isObject();
    }

    default boolean isZero() {
        return isNumericConstant() && getDoubleValue() == 0d;
    }

    default boolean isOne() {
        return isNumericConstant() && getDoubleValue() == 1d;
    }

    /// Provides the value of this argument as a double. Throws error when isConstant() is false.
    ///
    /// @return numeric value of this argument as a double
    default double getDoubleValue() {
        throw new MindcodeInternalError("Unexpected call to getDoubleValue");
    }

    /// Provides the value of this argument as a long. Throws error when isConstant() is false.
    ///
    /// @return numeric value of this argument as a long
    default long getLongValue() {
        return (long) getDoubleValue();
    }

    /// @return true when this value is a long
    default boolean isLong() {
        return isNumber() && getLongValue() == getDoubleValue();
    }

    /// Provides the value of this argument as an int. Throws error when isConstant() is false.
    ///
    /// @return numeric value of this argument as a long
    default int getIntValue() {
        return (int) getDoubleValue();
    }

    /// @return true when this value is an int
    default boolean isInteger() {
        return isNumber() && getIntValue() == getDoubleValue();
    }

    /// Provides the value of this argument as an object. Throws error when isConstant() is false.
    ///
    /// @return numeric value of this argument as an object
    default @Nullable Object getObject() {
        throw new MindcodeInternalError("Unexpected call to getObject");
    }

    /// Determines whether the value of this argument is a number. Throws error when isConstant() is false.
    ///
    /// @return true if this value is a number, false if it is an object.
    default boolean isNumber() {
        return !isObject();
    }

    /// Determines whether the value of this argument is an object. Throws error when isConstant() is false.
    ///
    /// @return true if this value is an object, false if it is a numeric value.
    default boolean isObject() {
        throw new MindcodeInternalError("Unexpected call to isObject");
    }
}
