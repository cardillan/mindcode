package info.teksol.mindcode.mindustry;

public class OptimizeSetThenOp implements LogicInstructionPipeline {
    private final LogicInstructionPipeline next;
    private State state;

    public OptimizeSetThenOp(LogicInstructionPipeline next) {
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

    private class EmptyState implements State {
        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction.isSet()) {
                return new ExpectOp(instruction);
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

    private class ExpectOp implements State {
        private final LogicInstruction set;

        ExpectOp(LogicInstruction set) {
            this.set = set;
        }

        @Override
        public State emit(LogicInstruction instruction) {
            if (!instruction.isOp()) {
                next.emit(set);
                next.emit(instruction);
                return new EmptyState();
            }

            switch (instruction.getArgs().size()) {
                case 3:
                    if (instruction.getArgs().get(2).equals(set.getArgs().get(0))) {
                        next.emit(
                                new LogicInstruction(
                                        instruction.getOpcode(),
                                        instruction.getArgs().get(0),
                                        instruction.getArgs().get(1),
                                        set.getArgs().get(1)
                                )
                        );
                        return new EmptyState();
                    } else {
                        next.emit(set);
                        next.emit(instruction);
                        return new EmptyState();
                    }

                case 4:
                    if (instruction.getArgs().get(2).equals(set.getArgs().get(0))) {
                        next.emit(
                                new LogicInstruction(
                                        instruction.getOpcode(),
                                        instruction.getArgs().get(0),
                                        instruction.getArgs().get(1),
                                        set.getArgs().get(1),
                                        instruction.getArgs().get(3)
                                        )
                        );
                        return new EmptyState();
                    } else if (instruction.getArgs().get(3).equals(set.getArgs().get(0))) {
                        next.emit(
                                new LogicInstruction(
                                        instruction.getOpcode(),
                                        instruction.getArgs().get(0),
                                        instruction.getArgs().get(1),
                                        instruction.getArgs().get(2),
                                        set.getArgs().get(1)
                                )
                        );
                        return new EmptyState();
                    } else {
                        next.emit(set);
                        next.emit(instruction);
                        return new EmptyState();
                    }

                default:
                    next.emit(set);
                    next.emit(instruction);
                    return new EmptyState();
            }
        }

        @Override
        public State flush() {
            return null;
        }
    }
}
