package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.JumpInstruction;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.OpInstruction;
import info.teksol.mindcode.logic.Opcode;

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
public class ImproveNegativeConditionalJumps extends PipelinedOptimizer {

    ImproveNegativeConditionalJumps(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    @Override
    protected State initialState() {
        return new EmptyState();
    }

    private final class EmptyState implements State {

        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction instanceof OpInstruction ix && isComparisonOperationToTmp(ix)) {
                return new ExpectJump(ix);
            } else {
                emitToNext(instruction);
                return this;
            }
        }

        @Override
        public State flush() {
            return this;
        }
    }

    private final class ExpectJump implements State {
        private final OpInstruction op;

        ExpectJump(OpInstruction op) {
            this.op = op;
        }

        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction instanceof JumpInstruction ix && ix.getCondition().equals("equal")) {
                // Other preconditions for the optimization
                boolean isSameVariable = ix.getFirstOperand().equals(op.getResult());
                boolean jumpComparesToFalse = ix.getSecondOperand().equals("false");

                if (isSameVariable && jumpComparesToFalse) {
                    if (!hasInverse(op.getOperation())) {
                        throw new OptimizationException("Unknown operation passed-in; can't find the inverse of [" + op.getOperation() + "]");
                    }

                    emitToNext(createInstruction(Opcode.JUMP, ix.getTarget(),
                            getInverse(op.getOperation()),
                            op.getFirstOperand(), op.getSecondOperand())
                    );
                    return new EmptyState();
                }
            }

            emitToNext(op);
            return new EmptyState().emit(instruction);
        }

        @Override
        public State flush() {
            emitToNext(op);
            return new EmptyState();
        }
    }

    private boolean isComparisonOperationToTmp(OpInstruction instruction) {
        return hasInverse(instruction.getOperation()) && isTemporary(instruction.getResult());
    }
}
