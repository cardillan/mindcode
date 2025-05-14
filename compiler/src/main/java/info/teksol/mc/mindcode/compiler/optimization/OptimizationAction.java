package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface OptimizationAction {

    /// AST context being processed by this optimization. On any given AST context, only one optimization may be
    /// performed. Optimizations of child contexts may be affected by optimizations on parent context.
    ///
    /// @return context containing the construct being optimized.
    AstContext astContext();

    /// Cost of the optimization in terms of additional instructions that will be generated (real, not virtual). The
    /// cost of non-inlining optimization must be positive -- optimizations with negative costs should be done
    /// automatically.
    ///
    /// If the optimization adds some instructions and removes others, this value is the net difference between the
    /// two values.
    ///
    /// @return cost of realizing this optimization
    int cost();

    /// Total benefit from realizing this optimization. Computed as sum of weights (from the AstContext) of all
    /// instructions multiplied by real instruction size that will be eliminated/avoided. Benefit computed this way may
    /// be altered to factor-in specific circumstances of given optimization, but ultimately needs to be based on
    /// instruction weights to remain compatible with other implementations.
    ///
    /// @return benefit of realizing this optimization
    double benefit();

    /// Performs the optimization represented by this action.
    ///
    /// @param costLimit actual cost limit for performing this optimization
    /// @return result of the optimization action.
    OptimizationResult apply(int costLimit);

    /// Returns the description of the optimization to be written to a log file for informational/debug purposes.
    ///
    /// @return description of the optimization
    String toString();

    /// When not null, identifies a group of mutually exclusive optimization actions this optimization is part of.
    /// All actions in a group with an efficiency better than any other action outside the group are considered.
    /// From these, the action with better overall benefit than the other considered actions is selected.
    ///
    /// @return an identifier of a group this action belongs to.
    default @Nullable String getGroup() { return null; }

    default double efficiency() {
        return benefit() / cost();
    }
}
