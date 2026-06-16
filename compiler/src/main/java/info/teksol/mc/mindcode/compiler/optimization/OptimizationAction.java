package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.profile.GenerationGoal;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface OptimizationAction {
    /// The goal applicable for the code region containing this action. The action is only selected
    /// when it matches the goal.
    GenerationGoal goal();

    /// The optimization that generates this action.
    Optimization optimization();

    /// AST context being processed by this optimization. On any given AST context, only one optimization may be
    /// performed. Optimizations of child contexts may be affected by optimizations of their parent context(s).
    ///
    /// @return context containing the construct being optimized.
    AstContext astContext();

    /// Cost of the optimization in terms of additional instructions that will be generated (real, not virtual size).
    ///
    /// If the optimization adds some instructions and removes others, this value is the net difference between the
    /// two values.
    ///
    /// @return cost of realizing this optimization
    int cost();

    default int positiveCost() {
        return Math.max(cost(), 0);
    }

    /// Total benefit from realizing this optimization. Computed as a sum of weights (from the AstContext) of all
    /// instructions multiplied by real instruction size that will be eliminated/avoided. Benefit computed this way
    /// may be altered to factor-in specific circumstances of a given optimization, but ultimately needs to be based
    /// on instruction weights to remain compatible with other implementations.
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

    default double speedEfficiency() {
        return benefit() / positiveCost();
    }

    default double sizeEfficiency() {
        return -cost();
    }

    default double neutralEfficiency() {
        return -cost() * benefit();
    }

    /// Returns the value of the metric relevant to this optimization's goal.
    default double efficiency() {
        return switch (goal()) {
            case SIZE -> sizeEfficiency();
            case NEUTRAL -> neutralEfficiency();
            case SPEED -> speedEfficiency();
        };

    }
    /// Returns `true` if the action improves at least one metric, and doesn't worsen the other.
    /// Such optimization should always be applied.
    default boolean totalImprovement() {
        return cost() <= 0 && benefit() >= 0.0 && improvement();
    }

    /// Returns `true` if the action improves at least one metric. Non-improving optimizations should not be applied.
    default boolean improvement() {
        return cost() < 0 || benefit() > 0.0;
    }

    /// Returns `true` if this action is better than the other in at least one metric, and not worse in the other.
    /// There's no point considering an optimization if there exists a mutually exclusive, strictly better one.
    default boolean isStrictlyBetterThan(OptimizationAction otherAction) {
        return cost() <= otherAction.cost() && benefit() > otherAction.benefit() ||
                cost() < otherAction.cost() && benefit() >= otherAction.benefit();

    }
}
