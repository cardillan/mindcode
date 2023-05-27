package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.JumpInstruction;
import info.teksol.mindcode.compiler.instructions.OpInstruction;
import info.teksol.mindcode.logic.Condition;

import static info.teksol.mindcode.logic.LogicBoolean.FALSE;

/**
 * Turns the following sequence of instructions:
 * <pre>{@code
 *   op <comparison> var1 A B
 *   jump label notEqual var2 false
 * }</pre>
 * into
 * <pre>{@code
 *   jump label <comparison> A B
 * }</pre>
 * Requirements:
 * <ol>
 * <li>jump condition is a notEqual comparison to {@code false}</li>
 * <li>{@code var1} and {@code var2} are identical</li>
 * <li>{@code var1} is a {@code __tmp} variable</li>
 * </ol>
 */
public class ImprovePositiveConditionalJumps extends BaseOptimizer {

    ImprovePositiveConditionalJumps(InstructionProcessor instructionProcessor) {
        super(instructionProcessor);
    }

    @Override
    protected boolean optimizeProgram() {
        try (LogicIterator iterator = createIterator()) {
            while (iterator.hasNext()) {
                if (iterator.next() instanceof OpInstruction op
                        && op.getOperation().toCondition() != null
                        && op.getResult().isTemporaryVariable()
                        && iterator.peek(0) instanceof JumpInstruction jump
                        && jump.getCondition() == Condition.NOT_EQUAL
                        && jump.getX().equals(op.getResult())
                        && jump.getY() == FALSE) {

                    iterator.remove();
                    iterator.next();        // skip the peeked instruction
                    iterator.set(createJump(jump.getAstContext(), jump.getTarget(),
                            op.getOperation().toCondition(), op.getX(), op.getY()));
                }
            }
        }

        return false;
    }
}
