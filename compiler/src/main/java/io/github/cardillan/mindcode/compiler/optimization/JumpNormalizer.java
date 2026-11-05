package io.github.cardillan.mindcode.compiler.optimization;

import io.github.cardillan.mindcode.compiler.optimization.DataFlowVariableStates.VariableStates;
import io.github.cardillan.mindcode.compiler.optimization.DataFlowVariableStates.VariableValue;
import io.github.cardillan.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import io.github.cardillan.mindcode.logic.arguments.*;
import io.github.cardillan.mindcode.logic.instructions.AssertBoundsInstruction;
import io.github.cardillan.mindcode.logic.instructions.JumpInstruction;
import io.github.cardillan.mindcode.logic.instructions.MultiJumpInstruction;
import io.github.cardillan.mindcode.logic.instructions.MultiLabelInstruction;
import io.github.cardillan.mindcode.messages.ERR;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static io.github.cardillan.mindcode.logic.arguments.LogicBoolean.FALSE;
import static io.github.cardillan.mindcode.logic.arguments.LogicBoolean.TRUE;

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
                    case MultiJumpInstruction ix when !ix.getJumpTable().isEmpty() -> {
                        LogicValue targetValue = findTargetValue(ix);
                        if (targetValue != null && targetValue.isNumericConstant()) {
                            int value = targetValue.getIntValue();
                            if (value < 0 || value >= ix.getJumpTable().size()) {
                                iterator.set(createEmpty(ix.getAstContext()));
                            } else {
                                LogicLabel logicLabel = ix.getJumpTable().get(value);
                                int index = firstInstructionIndex(iterator.nextIndex(),
                                        i -> i instanceof MultiLabelInstruction ml && ml.getLabel().equals(logicLabel));
                                if (index >= 0) {
                                    iterator.set(createJumpUnconditional(ix.getAstContext(), logicLabel));
                                    replaceInstruction(index, createLabel(instructionAt(index).getAstContext(), logicLabel));
                                }
                            }
                        }
                    }
                    default ->{ }
                }
            }
        }

        return false;
    }

    private @Nullable LogicValue findTargetValue(MultiJumpInstruction ix) {
        if (ix.getArg(0).isNumericConstant()) return (LogicValue) ix.getArg(0);
        if (ix.getArg(0) instanceof LogicVariable var) {
            VariableStates variableStates = getVariableStates(ix);
            if (variableStates != null && !optimizationContext.isStale(var)) {
                VariableValue variableValue = variableStates.findVariableValue(var);
                return variableValue == null ? null : variableValue.getConstantValue();
            }
        }
        return null;
    }
}
