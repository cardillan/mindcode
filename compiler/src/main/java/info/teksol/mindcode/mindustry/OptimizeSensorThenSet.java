package info.teksol.mindcode.mindustry;

class OptimizeSensorThenSet implements LogicInstructionPipeline {
    private final LogicInstructionPipeline next;
    private State state = new EmptyState();

    OptimizeSensorThenSet(LogicInstructionPipeline next) {
        this.next = next;
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
            if (instruction.isSensor()) {
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
        private final LogicInstruction sensor;

        ExpectSet(LogicInstruction sensor) {
            this.sensor = sensor;
        }

        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction.isSensor()) {
                next.emit(sensor);
                return new ExpectSet(instruction);
            }

            if (!instruction.isSet()) {
                next.emit(sensor);
                next.emit(instruction);
                return new EmptyState();
            }

            if (!sensor.getArgs().get(0).equals(instruction.getArgs().get(1))) {
                next.emit(sensor);
                next.emit(instruction);
                return new EmptyState();
            }

            next.emit(
                    new LogicInstruction(
                            sensor.getOpcode(),
                            instruction.getArgs().get(0),
                            sensor.getArgs().get(1),
                            sensor.getArgs().get(2)
                    )
            );
            return new EmptyState();
        }

        @Override
        public State flush() {
            next.emit(sensor);
            return new EmptyState();
        }
    }
}
