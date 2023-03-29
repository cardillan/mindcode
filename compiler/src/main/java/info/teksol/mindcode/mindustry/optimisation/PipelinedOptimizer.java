package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;

// Base class for pipeline-operating optimizers
// Derived classes only implement states and provide an initial state
abstract class PipelinedOptimizer extends BaseOptimizer {
    private State state = new UninitializedState();

    public PipelinedOptimizer(LogicInstructionPipeline next) {
        super(next);
    }
    
    protected abstract State initialState();

    @Override
    public void emit(LogicInstruction instruction) {
        state = state.emit(instruction);
    }

    @Override
    public void flush() {
        state = state.flush();
        next.flush();
    }

    protected interface State {
        State emit(LogicInstruction instruction);
        State flush();
    }
    
    private class UninitializedState implements State {
        @Override
        public State emit(LogicInstruction instruction) {
            return initialState().emit(instruction);
        }

        @Override
        public State flush() {
            return this;
        }
    }
}
