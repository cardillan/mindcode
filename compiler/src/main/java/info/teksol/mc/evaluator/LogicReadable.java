package info.teksol.mc.evaluator;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface LogicReadable {

    /// Indicates that an expressions using the value can be numerically evaluated at compile time.
    /// All literals can be evaluated. Built-in constants can only be evaluated if they aren't a variable
    /// (e.g. @coal can be evaluated, but @thisx cannot).
    ///
    /// @return true if the expression can be evaluated.
    boolean canEvaluate();

    double getDoubleValue();

    long getLongValue();

    @Nullable Object getObject();

    boolean isObject();
}
