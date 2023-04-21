package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.JumpInstruction;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.OpInstruction;
import info.teksol.mindcode.logic.ArgumentType;
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
public class ImprovePositiveConditionalJumps extends PipelinedOptimizer {

    ImprovePositiveConditionalJumps(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    @Override
    protected State initialState() {
        return new EmptyState();
    }

    private final class EmptyState implements State {

        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction instanceof OpInstruction ix && isComparisonOperatorToTmp(ix)) {
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
            if (instruction instanceof JumpInstruction ix && ix.getCondition() == Condition.NOT_EQUAL) {
                // Other preconditions for the optimization
                boolean isSameVariable = ix.getFirstOperand().equals(op.getResult());
                boolean jumpComparesToFalse = ix.getSecondOperand() == FALSE;

                if (isSameVariable && jumpComparesToFalse) {
                    emitToNext(createJump(ix.getTarget(),
                            op.getOperation().toCondition(),
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
    
    private boolean isComparisonOperatorToTmp(OpInstruction ix) {
        return ix.getOperation().toCondition() != null && ix.getResult().getType() == ArgumentType.TMP_VARIABLE;
    }
}
