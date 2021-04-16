package info.teksol.mindcode.mindustry;

class OptimizeReadThenSet implements LogicInstructionPipeline {
    private final LogicInstructionPipeline next;
    private State state;

    OptimizeReadThenSet(LogicInstructionPipeline next) {
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
            if (instruction.isRead()) {
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
        private final LogicInstruction read;

        ExpectSet(LogicInstruction read) {
            this.read = read;
        }

        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction.isRead()) {
                next.emit(read);
                return new ExpectSet(instruction);
            }

            if (!instruction.isSet()) {
                next.emit(read);
                next.emit(instruction);
                return new EmptyState();
            }

            if (!instruction.getArgs().get(1).equals(read.getArgs().get(0))) {
                next.emit(read);
                next.emit(instruction);
                return new EmptyState();
            }

            next.emit(
                    new LogicInstruction(
                            read.getOpcode(),
                            instruction.getArgs().get(0),
                            read.getArgs().get(1),
                            read.getArgs().get(2)
                    )
            );
            return new EmptyState();
        }

        @Override
        public State flush() {
            next.emit(read);
            return new EmptyState();
        }
    }
}
