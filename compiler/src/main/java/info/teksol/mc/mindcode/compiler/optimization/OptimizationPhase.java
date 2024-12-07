package info.teksol.mc.mindcode.compiler.optimization;

import java.util.List;

import static info.teksol.mc.mindcode.compiler.optimization.Optimization.*;

public enum OptimizationPhase {
    /**
     * Optimizers in this phase are run only once in the initial pass. Here should be optimizers completely independent
     * of other optimizers (i.e. they won't benefit from multiple passes).
     */
    INITIAL("Initial",
            CASE_EXPRESSION_OPTIMIZATION,
            DEAD_CODE_ELIMINATION,
            TEMP_VARIABLES_ELIMINATION
    ),

    /**
     * Optimizers that can benefit from other optimizers' modifications. Will be run in multiple passes
     * until no more changes are done. The AST context structure is kept updated for these optimizers.
     */
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
            IF_EXPRESSION_OPTIMIZATION,
            CASE_SWITCHING,
            RETURN_OPTIMIZATION
    ),

    /**
     * Optimizers run in a single pass at the end of the optimization. These optimizers can make changes incompatible
     * with the AST context structure, therefore are separated from the previous phase.
     */
    FINAL("Final",
            JUMP_NORMALIZATION,
            JUMP_STRAIGHTENING,
            JUMP_OPTIMIZATION,
            JUMP_THREADING,
            UNREACHABLE_CODE_ELIMINATION,
            DEAD_CODE_ELIMINATION,
            SINGLE_STEP_ELIMINATION,
            STACK_OPTIMIZATION,
            PRINT_MERGING
    );

    public final String name;
    public final List<Optimization> optimizations;

    OptimizationPhase(String name, Optimization... optimizations) {
        this.name = name;
        this.optimizations = List.of(optimizations);
    }
}
