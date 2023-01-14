package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

// Base class for global optimizers. Consumes the netire program before performing optimizations on it.
// Contains helper method to navigate and manipulate the program.
abstract class GlobalOptimizer extends BaseOptimizer {
    protected final List<LogicInstruction> program = new ArrayList<>();
    
    public GlobalOptimizer(LogicInstructionPipeline next) {
        super(next);
    }

    @Override
    public void emit(LogicInstruction instruction) {
        program.add(instruction);
    }

    @Override
    public void flush() {
        optimizeProgram();
        program.forEach(next::emit);
        next.flush();
    }
    
    protected abstract void optimizeProgram();

    // Starting at index, finds first instruction matching predicate.
    // Returns the index or -1 if not found.
    protected int findInstructionIndex(int index, Predicate<LogicInstruction> matcher) {
        for (int i = index; i < program.size(); i++) {
            LogicInstruction instruction = program.get(i);
            if (matcher.test(instruction)) {
                return i;
            }
        }
        
        return -1;
    }
    
    // Starting at index, find first instruction matching predicate. Return null if not found.
    protected LogicInstruction findInstruction(int index, Predicate<LogicInstruction> matcher) {
        int result = findInstructionIndex(index, matcher);
        return result < 0 ? null : program.get(result);
    }
    
}
