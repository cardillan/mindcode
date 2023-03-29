package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;

// Base class for pipeline-operating optimizers
// Derived classes only implement states and provide an initial state
abstract class PipelinedOptimizer extends BaseOptimizer {
    private State state = new UninitializedState();
    private int received = 0;
    private int emitted = 0;

    public PipelinedOptimizer(LogicInstructionPipeline next) {
        super(next);
    }
    
    protected abstract State initialState();

    @Override
    public final void emit(LogicInstruction instruction) {
        received++;
        state = state.emit(instruction);
    }

    @Override
    protected void emitToNext(LogicInstruction instruction) {
        emitted++;
        super.emitToNext(instruction);
    }

    @Override
    public final void flush() {
        state = state.flush();
        generateFinalMessages();
        super.flush();
    }

    protected void generateFinalMessages() {
        if (emitted != received) {
            emitMessage("%6d instructions eliminated by %s.", received - emitted, getClass().getSimpleName());
        }
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
