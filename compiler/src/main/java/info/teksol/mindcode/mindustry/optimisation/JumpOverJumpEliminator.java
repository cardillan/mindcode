package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import info.teksol.mindcode.mindustry.logic.Opcode;
import java.util.ArrayList;
import java.util.List;

// This optimizer detects situations where a conditional jump skips a following, unconditional one and replaces it
// with a single conditional jump with a reversed condition and a target of the second jump. Example:
//
// jump __label0 equal __tmp9 false
// jump __label1
// label __label0
//
// will be turned to
//
// jump __label1 notEqual __tmp9 false
//
// Optimization won't be done if the condition doesn't have an inverse (ie. ===).
//
// These sequences of instructions may arise when using break or continue statements:
//
// while true
//   ...
//   if not_alive
//     break
//   end
// end
public class JumpOverJumpEliminator extends PipelinedOptimizer {

    public JumpOverJumpEliminator(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    @Override
    protected State initialState() {
        return new EmptyState();
    }

    private final class EmptyState implements State {
        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction.isJump() && hasInverse(instruction.getArgs().get(1))) {
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
        private final LogicInstruction conditionalJump;

        public ExpectJump(LogicInstruction conditionalJump) {
            this.conditionalJump = conditionalJump;
        }

        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction.isJump() && instruction.getArgs().get(1).equals("always")) {
                return new ExpectLabel(conditionalJump, instruction);
            } else {
                emitToNext(conditionalJump);
                emitToNext(instruction);
                return new EmptyState();
            }
        }

        @Override
        public State flush() {
            emitToNext(conditionalJump);
            return new EmptyState();
        }
    }

    private final class ExpectLabel implements State {
        private final String targetLabel;
        private final LogicInstruction conditionalJump;
        private final LogicInstruction unconditionalJump;
        private final List<LogicInstruction> labels = new ArrayList<>();
        private boolean isJumpOverJump = false;

        public ExpectLabel(LogicInstruction conditionalJump, LogicInstruction unconditionalJump) {
            this.conditionalJump = conditionalJump;
            this.unconditionalJump = unconditionalJump;
            this.targetLabel = conditionalJump.getArgs().get(0);
        }

        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction.isLabel()) {
                if (instruction.getArgs().get(0).equals(targetLabel)) {
                    isJumpOverJump = true;
                }
                labels.add(instruction);
                return this;
            }

            if (isJumpOverJump) {
                emitToNext(
                        createInstruction(
                                Opcode.JUMP,
                                unconditionalJump.getArgs().get(0),
                                getInverse(conditionalJump.getArgs().get(1)),
                                conditionalJump.getArgs().get(2),
                                conditionalJump.getArgs().get(3)
                        )
                );
            } else {
                emitToNext(conditionalJump);
                emitToNext(unconditionalJump);
            }

            labels.forEach(JumpOverJumpEliminator.this::emitToNext);

            if (instruction.isJump() && hasInverse(instruction.getArgs().get(1))) {
                return new ExpectJump(instruction);
            } else {
                emitToNext(instruction);
                return new EmptyState();
            }
        }

        @Override
        public State flush() {
            emitToNext(conditionalJump);
            emitToNext(unconditionalJump);
            labels.forEach(JumpOverJumpEliminator.this::emitToNext);
            return new EmptyState();
        }
    }
}
