package info.teksol.mindcode.mindustry;

import java.util.ArrayList;
import java.util.List;

class OptimizeOpThenSet implements LogicInstructionPipeline {
    private final LogicInstructionPipeline next;
    private State state;

    OptimizeOpThenSet(LogicInstructionPipeline next) {
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
            if (instruction.isOp()) {
                return new ExpectSet(instruction);
            } else {
                next.emit(instruction);
                return this;
            }
        }

        @Override
        public State flush() {
            next.flush();
            return this;
        }
    }

    private final class ExpectSet implements State {
        private final LogicInstruction op;

        ExpectSet(LogicInstruction op) {
            this.op = op;
        }

        @Override
        public State emit(LogicInstruction instruction) {
            if (!instruction.isSet()) {
                next.emit(op);
                if (instruction.isOp()) {
                    return new ExpectSet(instruction);
                } else {
                    next.emit(instruction);
                    return new EmptyState();
                }
            }

            if (!instruction.getArgs().get(1).equals(op.getArgs().get(1))) {
                next.emit(op);
                next.emit(instruction);
                return new EmptyState();
            }

            final List<String> newArgs = new ArrayList<>(op.getArgs());
            newArgs.set(1, instruction.getArgs().get(0));
            next.emit(new LogicInstruction(op.getOpcode(), newArgs));
            return new EmptyState();
        }

        @Override
        public State flush() {
            next.emit(op);
            return new EmptyState();
        }
    }
}
