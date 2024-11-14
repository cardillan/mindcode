package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mindcode.logic.Condition;
import info.teksol.mindcode.logic.Operation;

import java.util.List;

import static info.teksol.mindcode.logic.LogicBoolean.FALSE;

/**
 * Turns the following sequence of instructions:
 * <pre>{@code
 *   op <comparison> var1 A B
 *   jump label notEqual|equal var2 false
 * }</pre>
 * into
 * <pre>{@code
 *   jump label <comparison|inverse of comparison> A B
 * }</pre>
 * Requirements:
 * <ol>
 * <li>jump condition is an notEqual/equal comparison to {@code false}</li>
 * <li>{@code var1} and {@code var2} are identical</li>
 * <li>{@code var1} is a {@code __tmp} variable</li>
 * <li>{@code var1} is used only in the two affected instructions</li>
 * <li>{@code <comparison>} can be converted to condition/has an inverse</li>
 * </ol>
 */
class JumpOptimizer extends BaseOptimizer {
    private OpInstruction lastOp;
    private int lastOpIndex;

    JumpOptimizer(OptimizationContext optimizationContext) {
        super(Optimization.JUMP_OPTIMIZATION, optimizationContext);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {

        try (LogicIterator iterator = createIterator()) {
            while (iterator.hasNext()) {
                LogicInstruction instruction = iterator.next();
                if (instruction instanceof OpInstruction op) {
                    lastOp = op;
                    lastOpIndex = iterator.currentIndex();
                } else if (lastOp != null
                        && instruction instanceof JumpInstruction jump
                        && jump.isConditional()
                        && lastOp.getResult().isTemporaryVariable()
                        && jump.getX().equals(lastOp.getResult())
                        && jump.getY() == FALSE) {

                    List<LogicInstruction> list = instructions(
                            ix -> ix.getArgs().contains(lastOp.getResult()) && !(ix instanceof PushOrPopInstruction));

                    // Not exactly two instructions
                    if (list.size() == 2) {
                        Operation operation = jump.getCondition() == Condition.EQUAL
                                ? lastOp.getOperation().hasInverse() ? lastOp.getOperation().inverse() : null
                                : lastOp.getOperation().toCondition() != null ? lastOp.getOperation() : null;

                        if (operation != null) {
                            iterator.set(createJump(jump.getAstContext(), jump.getTarget(), operation.toCondition(),
                                    lastOp.getX(), lastOp.getY()));
                            removeInstruction(lastOpIndex);
                        }
                    }
                } else if (instruction instanceof LabelInstruction label) {
                    if (optimizationContext.isActive(label.getLabel())) {
                        lastOp = null;
                    }
                } else if (!(instruction instanceof NoOpInstruction)) {
                    lastOp = null;
                }

            }

            return false;
        }
    }
}