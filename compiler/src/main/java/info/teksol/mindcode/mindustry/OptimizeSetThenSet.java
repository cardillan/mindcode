package info.teksol.mindcode.mindustry;

class OptimizeSetThenSet implements LogicInstructionPipeline {
    private final LogicInstructionPipeline next;
    private State state;

    OptimizeSetThenSet(LogicInstructionPipeline next) {
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
        next.flush();
    }

    private interface State {
        State emit(LogicInstruction instruction);

        State flush();
    }

    private final class EmptyState implements State {
        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction.isSet()) {
                return new ExpectSet(instruction);
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

    private final class ExpectSet implements State {
        private final LogicInstruction previousSet;

        ExpectSet(LogicInstruction previousSet) {
            this.previousSet = previousSet;
        }

        @Override
        public State emit(LogicInstruction instruction) {
            if (!instruction.isSet()) {
                next.emit(previousSet);
                next.emit(instruction);
                return new EmptyState();
            }

            if (!previousSet.getArgs().get(0).equals(instruction.getArgs().get(1))) {
                next.emit(previousSet);
                return new ExpectSet(instruction);
            }

            next.emit(
                    new LogicInstruction(
                            instruction.getOpcode(),
                            instruction.getArgs().get(0),
                            previousSet.getArgs().get(1)
                    )
            );
            return new EmptyState();
        }

        @Override
        public State flush() {
            next.emit(previousSet);
            return new EmptyState();
        }
    }
}
