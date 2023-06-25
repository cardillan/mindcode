package info.teksol.mindcode.compiler.optimization;

import java.util.List;

import static info.teksol.mindcode.compiler.optimization.Optimization.*;

public enum OptimizationPhase {
    /**
     * Optimizers in this phase are run only once in the initial pass. Here should be optimizers completely independent
     * of other optimizers (i.e. they won't benefit from multiple passes).
     */
    INITIAL("Initial",
            TEMP_VARIABLES_ELIMINATION,
            CASE_EXPRESSION_OPTIMIZATION,
            DEAD_CODE_ELIMINATION
    ),

    /**
     * Optimizers that can benefit from other optimizers' modifications. Will be run in multiple passes
     * until no more changes are done. The AST context structure is kept updated for these optimizers.
     */
    ITERATED("Iterated",
            CONDITIONAL_JUMPS_NORMALIZATION,
            CONDITIONAL_JUMPS_IMPROVEMENT,
            SINGLE_STEP_JUMP_ELIMINATION,
            EXPRESSION_OPTIMIZATION,
            IF_EXPRESSION_OPTIMIZATION,
            DATA_FLOW_OPTIMIZATION,
            LOOP_OPTIMIZATION,
            LOOP_UNROLLING
    ),

    /**
     * Optimizers run in a single pass at the end of the optimization. These optimizers can make changes incompatible
     * with the AST context structure, therefore are separated from the previous phase.
     */
    FINAL("Final",
            CONDITIONAL_JUMPS_NORMALIZATION,
            DEAD_CODE_ELIMINATION,
            JUMP_OVER_JUMP_ELIMINATION,
            CONDITIONAL_JUMPS_IMPROVEMENT,
            JUMP_TARGET_PROPAGATION,
            UNREACHABLE_CODE_ELIMINATION,
            SINGLE_STEP_JUMP_ELIMINATION,
            STACK_USAGE_OPTIMIZATION,
            PRINT_TEXT_MERGING
    );

    public final String name;
    public final List<Optimization> optimizations;

    OptimizationPhase(String name, Optimization... optimizations) {
        this.name = name;
        this.optimizations = List.of(optimizations);
    }
}
