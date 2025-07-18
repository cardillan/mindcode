package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mc.mindcode.logic.arguments.LogicBoolean;
import info.teksol.mc.mindcode.logic.instructions.JumpInstruction;
import org.jspecify.annotations.NullMarked;

import static info.teksol.mc.mindcode.logic.arguments.LogicBoolean.FALSE;
import static info.teksol.mc.mindcode.logic.arguments.LogicBoolean.TRUE;

/// Replaces conditional jumps whose condition is always true with unconditional jumps
/// and completely removes jumps that are always false.
@NullMarked
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
                    LogicBoolean result = expressionEvaluator.evaluateConditionalInstruction(jump);

                    if (result == TRUE) {
                        iterator.set(createJumpUnconditional(jump.getAstContext(), jump.getTarget()));
                    } else if (result == FALSE) {
                        iterator.set(createEmpty(jump.getAstContext()));
                    }
                }
            }
        }

        return false;
    }
}
