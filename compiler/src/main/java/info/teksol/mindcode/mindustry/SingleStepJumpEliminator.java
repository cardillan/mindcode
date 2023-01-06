package info.teksol.mindcode.mindustry;

import java.util.ArrayList;
import java.util.List;

// Remove jumps (both conditional and unconditional) that target the next instruction.
// Technivally, if we have a sequence
//   0: jump 2 ...
//   1: jump 2 ...
//   2: ...
// we could eliminate both jumps. This class will only remove the second jump, because before that removal the first
// one doesn't target the next instruction. However, such structures probably only arise in some degenerate cases,
// such as "if a if b end end"
// (If the sequence is 0: jump 1, 1: jum 2, both jumps will be eliminated.)
class SingleStepJumpEliminator implements LogicInstructionPipeline {
    private final LogicInstructionPipeline next;
    private State state;

    SingleStepJumpEliminator(LogicInstructionPipeline next) {
        this.next = next;
        this.state = new EmptyState();
    }

    @Override
    public void emit(LogicInstruction instruction) {
        state = state.emit(instruction);
    }

    @Override
    public void flush() {
        state = state.flush();
    }

    private interface State {
        State emit(LogicInstruction instruction);

        State flush();
    }

    private final class EmptyState implements State {
        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction.isJump()) {
                return new ExpectLabel(instruction);
            } else {
                next.emit(instruction);
                return this;
            }
        }

        @Override
        public State flush() {
            return this;
        }
    }

    private final class ExpectLabel implements State {
        private final LogicInstruction jump;
        private final String targetLabel;
        private final List<LogicInstruction> labels = new ArrayList<>();
        private boolean targetLabelEncountered = false;

        ExpectLabel(LogicInstruction jump) {
            this.jump = jump;
            this.targetLabel = jump.getArgs().get(0);
        }

        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction.isLabel()) {
                if (instruction.getArgs().get(0).equals(targetLabel)) {
                    targetLabelEncountered = true;
                }
                labels.add(instruction);
                return this;
            }
            
            // If we didn't encounter targetLabel, the jump leads somewhere else than to this instrcution
            // and canot be removed.
            if (!targetLabelEncountered) {
                next.emit(jump);
            }
            
            labels.forEach(next::emit);
            
            if (instruction.isJump()) {
                return new ExpectLabel(instruction);
            }
            else {
                next.emit(instruction);
                return new EmptyState();
            }
        }

        @Override
        public State flush() {
            next.emit(jump);
            labels.forEach(next::emit);
            return new EmptyState();
        }
    }
}
