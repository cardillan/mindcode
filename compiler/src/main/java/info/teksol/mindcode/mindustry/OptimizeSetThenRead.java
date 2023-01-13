package info.teksol.mindcode.mindustry;

class OptimizeSetThenRead implements LogicInstructionPipeline {
    private final LogicInstructionPipeline next;
    private State state;

    OptimizeSetThenRead(LogicInstructionPipeline next) {
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
                if (!instruction.getArgs().get(0).startsWith(LogicInstructionGenerator.TMP_PREFIX)) {
                    next.emit(instruction);
                    return this;
                } else {
                    return new ExpectRead(instruction);
                }
            }

            next.emit(instruction);
            return this;
        }

        @Override
        public State flush() {
            return this;
        }
    }

    private final class ExpectRead implements State {
        private final LogicInstruction set;

        ExpectRead(LogicInstruction set) {
            this.set = set;
        }

        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction.isSet()) {
                if (!instruction.getArgs().get(0).startsWith(LogicInstructionGenerator.TMP_PREFIX)) {
                    next.emit(set);
                    next.emit(instruction);
                    return new EmptyState();
                } else {
                    next.emit(set);
                    return new ExpectRead(instruction);
                }
            }

            if (!instruction.isRead()) {
                next.emit(set);
                next.emit(instruction);
                return new EmptyState();
            }

            if (!instruction.getArgs().get(2).equals(set.getArgs().get(0))) {
                next.emit(set);
                next.emit(instruction);
                return new EmptyState();
            }

            next.emit(
                    new LogicInstruction(
                            instruction.getOpcode(),
                            instruction.getArgs().get(0),
                            instruction.getArgs().get(1),
                            set.getArgs().get(1)
                    )
            );

            return new EmptyState();
        }

        @Override
        public State flush() {
            next.emit(set);
            return new EmptyState();
        }
    }
}
