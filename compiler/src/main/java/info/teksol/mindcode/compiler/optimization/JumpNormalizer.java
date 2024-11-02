package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.JumpInstruction;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mindcode.logic.LogicBoolean;

import static info.teksol.mindcode.logic.LogicBoolean.FALSE;
import static info.teksol.mindcode.logic.LogicBoolean.TRUE;

/**
 * Replaces conditional jumps whose condition is always true with unconditional jumps
 * and completely removes jumps that are always false.
 */
class JumpNormalizer extends BaseOptimizer {

    private final OptimizerExpressionEvaluator expressionEvaluator;

    public JumpNormalizer(OptimizationContext optimizationContext) {
        super(Optimization.JUMP_NORMALIZATION, optimizationContext);
        expressionEvaluator = new OptimizerExpressionEvaluator(instructionProcessor);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        try (LogicIterator iterator = createIterator()) {
            while (iterator.hasNext()) {
                if (iterator.next() instanceof JumpInstruction jump && jump.isConditional()) {
                    LogicBoolean result = expressionEvaluator.evaluateJumpInstruction(jump);

                    if (result == TRUE) {
                        iterator.set(createJumpUnconditional(jump.getAstContext(), jump.getTarget()));
                    } else if (result == FALSE) {
                        iterator.set(createNoOp(jump.getAstContext()));
                    }
                }
            }
        }

        return false;
    }
}
