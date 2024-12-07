package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;

public interface OptimizationAction {

    /**
     * AST context being processed by this optimization. On any given AST context, only one optimization may be
     * performed. Optimizations of child contexts may be affected by optimizations on parent context.
     *
     * @return context containing the construct being optimized.
     */
    AstContext astContext();

    /**
     * Indicates this optimization is a function inlining proposal. Inlining of each call site must be proposed
     * separately. The optimization coordinator may choose to inline only some of the function calls. Additional
     * savings from inlining all invocations of the function are computed by the coordinator.
     * <p/>
     * Cost of inlining a function is the size of the function body minus the size of the call and return instructions.
     * Benefits are the CALL and RETURN instructions and, in an individual call, each SET instruction assigning a
     * variable (not a constant) to the argument (when inlining, these SET instructions will be saved).
     * <p/>
     * For inline functions, the cost can be non-positive, for example when inlining a single-instruction function.
     *
     * @return true if this proposal concerns function inlining
     */
    boolean inlining();

    /**
     * For inlining proposals, returns the function prefix of the function being inlined. Will not be called for
     * a non-inlining proposal.
     *
     * @return function prefix of the inlined function
     */
    String functionPrefix();

    /**
     * Cost of the optimization in terms of additional instructions that will be generated (real, not virtual). The
     * cost of non-inlining optimization must be positive -- optimizations with negative costs should be done
     * automatically.
     * <p/>
     * If the optimization adds some instructions and removes others, this value is the net difference between the
     * two values.
     *
     * @return cost of realizing this optimization
     */
    int cost();

    /**
     * Total benefit from realizing this optimization. Computed as sum of weights (from the AstContext) of all
     * instructions multiplied by real instruction size that will be eliminated/avoided. Benefit computed this way may
     * be altered to factor-in specific circumstances of given optimization, but ultimately needs to be based on
     * instruction weights to remain compatible with other implementations.
     *
     * @return benefit of realizing this optimization
     */
    double benefit();

    /**
     * Performs the optimization represented by this action.
     *
     * @param costLimit actual cost limit for performing this optimization
     * @return result of the optimization action.
     */
    OptimizationResult apply(int costLimit);

    /**
     * Returns the description of the optimization to be written to a log file for informational/debug purposes.
     *
     * @return description of the optimization
     */
    String toString();

    default double efficiency() {
        return benefit() / cost();
    }
}
