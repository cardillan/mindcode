package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.profile.GenerationGoal;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface Optimizer {

    String getName();

    Optimization getOptimization();

    void setLevel(OptimizationLevel level);

    void setGoal(GenerationGoal goal);

    void setDebugPrinter(DebugPrinter debugPrinter);

    /// Performs general optimizations of the program. All optimizations that do not increase program size can be
    /// performed here.
    ///
    /// @param phase current optimization phase
    /// @param pass current optimization pass
    /// @return true if the program was actually modified
    boolean optimize(OptimizationPhase phase, int pass);

    /// Creates a list of possible optimizations. It doesn't make sense to propose optimizations with cost higher
    /// than the limit - there isn't enough remaining space to use.
    ///
    /// When a set - or multiple sets - of mutually exclusive optimizations are produced, none of the sets
    /// may contain an action which has the same cost or higher cost as another action, while having the same
    /// or smaller benefit than the other action. in other words, when ordering the actions in the set by increasing
    /// costs, the benefits must also increase. The efficiency in such an ordered set of action, on the other hand,
    /// may decrease.
    ///
    /// @return list of possible optimizations
    List<OptimizationAction> getPossibleOptimizations(int costLimit);

    void generateFinalMessages();
}
