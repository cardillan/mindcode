package info.teksol.mindcode.mindustry;

class OptimizeSetThenPrint implements LogicInstructionPipeline {
    private final LogicInstructionPipeline next;
    private State state;

    OptimizeSetThenPrint(LogicInstructionPipeline next) {
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
            if (instruction.isSet()) {
                return new ExpectPrint(instruction);
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

    private final class ExpectPrint implements State {
        private final LogicInstruction set;

        ExpectPrint(LogicInstruction set) {
            this.set = set;
        }

        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction.isSet()) {
                next.emit(set);
                return new ExpectPrint(instruction);
            }

            if (!instruction.isPrint()) {
                next.emit(set);
                next.emit(instruction);
                return new EmptyState();
            }

            if (!instruction.getArgs().get(0).equals(set.getArgs().get(0))) {
                next.emit(set);
                next.emit(instruction);
                return new EmptyState();
            }

            next.emit(
                    new LogicInstruction(
                            instruction.getOpcode(),
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
