package info.teksol.mc.mindcode.compiler.optimization;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum OptimizationResult {
    /// The optimization was done as planned.
    REALIZED,

    /// The optimization proposal is no longer valid.
    INVALID,

    /// The optimization is possible, but would be costlier than initially assessed. This can only happen
    /// if the code was changed by some other optimizer in the meantime.
    OVER_LIMIT
}
