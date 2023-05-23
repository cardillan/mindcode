package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.JumpInstruction;
import info.teksol.mindcode.compiler.instructions.OpInstruction;
import info.teksol.mindcode.logic.ArgumentType;
import info.teksol.mindcode.logic.Condition;

import static info.teksol.mindcode.logic.LogicBoolean.FALSE;

/**
 * Turns the following sequence of instructions:
 * <pre>{@code
 *   op <comparison> var1 A B
 *   jump label equal var2 false
 * }</pre>
 * into
 * <pre>{@code
 *   jump label <inverse of comparison> A B
 * }</pre>
 * Requirements:
 * <ol>
 * <li>jump condition is an equal comparison to {@code false}</li>
 * <li>{@code var1} and {@code var2} are identical</li>
 * <li>{@code var1} is a {@code __tmp} variable</li>
 * <li>{@code <comparison>} has an inverse</li>
 * </ol>
 */
public class ImproveNegativeConditionalJumps extends BaseOptimizer {

    ImproveNegativeConditionalJumps(InstructionProcessor instructionProcessor) {
        super(instructionProcessor);
    }

    @Override
    protected boolean optimizeProgram() {
        try (LogicIterator iterator = createIterator()) {
            while (iterator.hasNext()) {
                if (iterator.next() instanceof OpInstruction op
                        && op.getOperation().hasInverse()
                        && op.getResult().getType() == ArgumentType.TMP_VARIABLE
                        && iterator.peek(0) instanceof JumpInstruction jump
                        && jump.getCondition() == Condition.EQUAL
                        && jump.getX().equals(op.getResult())
                        && jump.getY() == FALSE) {

                    iterator.remove();
                    iterator.next();        // skip the peeked instruction
                    iterator.set(createJump(jump.getAstContext(), jump.getTarget(),
                            op.getOperation().inverse().toCondition(), op.getX(), op.getY()));
                }
            }
        }

        return false;
    }
}
