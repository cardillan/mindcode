package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.JumpInstruction;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.OpInstruction;
import info.teksol.mindcode.compiler.instructions.PushOrPopInstruction;
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
public class ConditionalJumpsOptimizer extends BaseOptimizer {

    ConditionalJumpsOptimizer(OptimizationContext optimizationContext) {
        super(Optimization.CONDITIONAL_JUMPS_OPTIMIZATION, optimizationContext);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        try (LogicIterator iterator = createIterator()) {
            while (iterator.hasNext()) {
                if (iterator.next() instanceof OpInstruction op
                        && iterator.peek(0) instanceof JumpInstruction jump
                        && jump.isConditional()
                        && op.getResult().isTemporaryVariable()
                        && jump.getX().equals(op.getResult())
                        && jump.getY() == FALSE) {

                    List<LogicInstruction> list = instructions(
                            ix -> ix.getArgs().contains(op.getResult()) && !(ix instanceof PushOrPopInstruction));

                    // Not exactly two instructions
                    if (list.size() == 2) {
                        Operation operation = jump.getCondition() == Condition.EQUAL
                                ? op.getOperation().hasInverse() ? op.getOperation().inverse() : null
                                : op.getOperation().toCondition() != null ? op.getOperation() : null;

                        if (operation != null) {
                            iterator.remove();
                            iterator.next();        // skip the peeked instruction
                            iterator.set(createJump(jump.getAstContext(), jump.getTarget(), operation.toCondition(),
                                    op.getX(), op.getY()));
                        }
                    }
                }
            }
        }

        return false;
    }
}
