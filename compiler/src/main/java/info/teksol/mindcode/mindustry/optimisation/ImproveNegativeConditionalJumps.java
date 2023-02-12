package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import info.teksol.mindcode.mindustry.logic.Opcode;

// Turns the following sequence of instructions:
//    op <comparison> var1 A B
//    jump label equal var2 false
//
// into
//    jump label <inverse of comparison> A B
//
// Requirements:
// 1. jump is an equal comparison to false
// 2. var1 and var2 are identical
// 3. var1 is a __tmp variable
// 4. <comparison> has an inverse
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

            // Other preconditions for the optimisation
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
        return hasInverse(instruction.getArgs().get(0)) &&
                instruction.getArgs().get(1).startsWith(instructionProcessor.getTempPrefix());
    }
}
