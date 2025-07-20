package info.teksol.mc.mindcode.compiler.optimization;

import org.jspecify.annotations.NullMarked;

import java.util.List;

import static info.teksol.mc.mindcode.compiler.optimization.Optimization.*;

@NullMarked
public enum OptimizationPhase {
    /// Optimizers in this phase are run only once in the initial pass. Here should be optimizers completely independent
    /// of other optimizers (i.e. they won't benefit from multiple passes).
    INITIAL("Initial",
            CASE_EXPRESSION_OPTIMIZATION,
            DEAD_CODE_ELIMINATION,
            TEMP_VARIABLES_ELIMINATION
    ),

    /// Optimizers that can benefit from other optimizers' modifications. Will be run in multiple passes
    /// until no more changes are done. The AST context structure is kept updated for these optimizers.
    ITERATED("Iterated",
            JUMP_NORMALIZATION,
            JUMP_STRAIGHTENING,
            JUMP_OPTIMIZATION,
            SINGLE_STEP_ELIMINATION,
            EXPRESSION_OPTIMIZATION,
            DATA_FLOW_OPTIMIZATION,
            LOOP_HOISTING,
            LOOP_OPTIMIZATION,
            LOOP_UNROLLING,
            FUNCTION_INLINING,
            ARRAY_OPTIMIZATION,
            IF_EXPRESSION_OPTIMIZATION,
            CASE_SWITCHING,
            RETURN_OPTIMIZATION
    ),

    /// Jump-specific optimizations. Several passes might be needed to iron out all jump paths.
    /// These optimizers can make changes incompatible with the AST context structure in this phase,
    /// therefore, are separated from the previous phase.
    JUMPS("Jumps",
            JUMP_NORMALIZATION,
            JUMP_STRAIGHTENING,
            JUMP_OPTIMIZATION,
            JUMP_THREADING,
            UNREACHABLE_CODE_ELIMINATION,
            DEAD_CODE_ELIMINATION,
            SINGLE_STEP_ELIMINATION
    ),

    /// Optimizers run in a single pass at the end of the optimization. No context structure dependency.
    FINAL("Final",
            STACK_OPTIMIZATION,
            PRINT_MERGING
    );

    public final String name;
    public final List<Optimization> optimizations;

    OptimizationPhase(String name, Optimization... optimizations) {
        this.name = name;
        this.optimizations = List.of(optimizations);
    }

    public boolean breaksContextStructure() {
        return this.ordinal() >= JUMPS.ordinal();
    }
}
