package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mc.mindcode.logic.arguments.LogicBoolean;
import info.teksol.mc.mindcode.logic.arguments.LogicLiteral;
import info.teksol.mc.mindcode.logic.instructions.AssertBoundsInstruction;
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
                switch (iterator.next()) {
                    case AssertBoundsInstruction ix when !ix.isRuntimeError() && ix.getLowerBound().isConstant()
                            && ix.getValue().isConstant() && ix.getUpperBound().isConstant() -> {
                        LogicLiteral low = expressionEvaluator.evaluate(ix.sourcePosition(), ix.getLowerCondition().toOperation(), ix.getLowerBound(), ix.getValue());
                        LogicLiteral high = expressionEvaluator.evaluate(ix.sourcePosition(), ix.getUpperCondition().toOperation(), ix.getValue(), ix.getUpperBound());
                        if (low == LogicBoolean.TRUE && high == LogicBoolean.TRUE) {
                            iterator.set(createEmpty(ix.getAstContext()));
                        } else if (low == LogicBoolean.FALSE || high == LogicBoolean.FALSE) {
                            ix.setRuntimeError();
                            error(ix.sourcePosition(), ERR.RUNTIME_CHECK_FAILED,
                                    ix.getMessage().format(instructionProcessor), ix.getValue().format(instructionProcessor));
                        }
                    }
                    case JumpInstruction jump when jump.isConditional() -> {
                        LogicBoolean result = expressionEvaluator.evaluateConditionalInstruction(jump);

                        if (result == TRUE) {
                            iterator.set(createJumpUnconditional(jump.getAstContext(), jump.getTarget()));
                        } else if (result == FALSE) {
                            iterator.set(createEmpty(jump.getAstContext()));
                        }
                    }
                    default ->{ }
                }
            }
        }

        return false;
    }
}
