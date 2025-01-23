package info.teksol.mc.evaluator;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface LogicReadable {
    /// Indicates that an expressions using the value can be evaluated at compile time.
    /// All literals can be evaluated. Built-in constants can only be evaluated if they aren't a variable
    /// (e.g. @coal can be evaluated, but @thisx cannot).
    ///
    /// @return true if the expression can be evaluated.
    boolean isConstant();

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
        throw new MindcodeInternalError("Unexpected call to getLongValue");
    }

    /// Provides the value of this argument as an object. Throws error when isConstant() is false.
    ///
    /// @return numeric value of this argument as an object
    default @Nullable Object getObject() {
        throw new MindcodeInternalError("Unexpected call to getObject");
    }

    /// Determines whether the value of this argument is an object. Throws error when isConstant() is false.
    ///
    /// @return true if this value is an object, false if it is a numeric value.
    default boolean isObject() {
        throw new MindcodeInternalError("Unexpected call to isObject");
    }
}
