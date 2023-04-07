package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
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
            if (instruction.isOp() && isComparisonOperatorToTmp(instruction)) {
                return new ExpectJump(instruction);
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
        private final LogicInstruction op;

        ExpectJump(LogicInstruction op) {
            this.op = op;
        }

        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction.isOp()) {
                if (!isComparisonOperatorToTmp(instruction)) {
                    emitToNext(op);
                    emitToNext(instruction);
                    return new EmptyState();
                } else {
                    emitToNext(op);
                    return new ExpectJump(instruction);
                }
            }

            // Not a conditional jump
            if (!instruction.isJump() || !instruction.getArgs().get(1).equals("equal")) {
                emitToNext(op);
                emitToNext(instruction);
                return new EmptyState();
            }

            // Other preconditions for the optimization
            boolean isSameVariable = instruction.getArgs().get(2).equals(op.getArgs().get(1));
            boolean jumpComparesToFalse = instruction.getArgs().get(3).equals("false");

            if (isSameVariable && jumpComparesToFalse) {
                if (!hasInverse(op.getArgs().get(0))) {
                    throw new OptimizationException("Unknown operation passed-in; can't find the inverse of [" + op.getArgs().get(0) + "]");
                }

                emitToNext(
                        createInstruction(
                                Opcode.JUMP,
                                instruction.getArgs().get(0),
                                getInverse(op.getArgs().get(0)),
                                op.getArgs().get(2),
                                op.getArgs().get(3)
                        )
                );
                return new EmptyState();
            }

            emitToNext(op);
            emitToNext(instruction);
            return new EmptyState();
        }


        @Override
        public State flush() {
            emitToNext(op);
            return new EmptyState();
        }

    }

    private boolean isComparisonOperatorToTmp(LogicInstruction instruction) {
        return hasInverse(instruction.getArg(0)) && isTemporary(instruction.getArg(1));
    }
}
