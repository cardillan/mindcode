package info.teksol.mindcode.mindustry;

class OptimizeGetlinkThenSet implements LogicInstructionPipeline {
    private final LogicInstructionPipeline next;
    private State state = new EmptyState();

    OptimizeGetlinkThenSet(LogicInstructionPipeline next) {
        this.next = next;
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
            if (instruction.isGetlink()) {
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
        private final LogicInstruction getlink;

        ExpectSet(LogicInstruction getlink) {
            this.getlink = getlink;
        }

        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction.isGetlink()) {
                next.emit(getlink);
                return new ExpectSet(instruction);
            }

            if (!instruction.isSet()) {
                next.emit(getlink);
                next.emit(instruction);
                return new EmptyState();
            }

            if (!getlink.getArgs().get(0).equals(instruction.getArgs().get(1))) {
                next.emit(getlink);
                next.emit(instruction);
                return new EmptyState();
            }

            next.emit(
                    new LogicInstruction(
                            getlink.getOpcode(),
                            instruction.getArgs().get(0),
                            getlink.getArgs().get(1)
                    )
            );

            return new EmptyState();
        }

        @Override
        public State flush() {
            next.emit(getlink);
            return new EmptyState();
        }
    }
}
