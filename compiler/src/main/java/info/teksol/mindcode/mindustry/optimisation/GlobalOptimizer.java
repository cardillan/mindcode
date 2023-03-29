package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


// Base class for global optimizers. Consumes the netire program before performing optimizations on it.
// Contains helper method to navigate and manipulate the program.
abstract class GlobalOptimizer extends BaseOptimizer {
    protected final List<LogicInstruction> program = new ArrayList<>();
    private int original;
    private int emitted;
    
    public GlobalOptimizer(LogicInstructionPipeline next) {
        super(next);
    }

    @Override
    public final void emit(LogicInstruction instruction) {
        program.add(instruction);
    }

    @Override
    public final void flush() {
        original = (int) program.stream().filter(ix -> !ix.isLabel()).count();
        optimizeProgram();
        emitted = (int) program.stream().filter(ix -> !ix.isLabel()).count();

        generateFinalMessages();
        
        program.forEach(this::emitToNext);
        program.clear();
        super.flush();
    }
    
    protected abstract void optimizeProgram();

    protected void generateFinalMessages() {
        if (emitted != original) {
            emitMessage("%6d instructions eliminated by %s.", original - emitted, getClass().getSimpleName());
        }
    }

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
    
    // Return list of instructions matching predicate
    protected List<LogicInstruction> findInstructions(Predicate<LogicInstruction> matcher) {
        return program.stream().filter(matcher).collect(Collectors.toList());
    }

    // Return the number of instructions matching predicate
    protected int countInstructions(Predicate<LogicInstruction> matcher) {
        return (int) program.stream().filter(matcher).count();
    }
    
    protected void replaceInstruction(LogicInstruction original, LogicInstruction replaced) {
        for (int index = 0; index < program.size(); index++) {
            if (program.get(index) == original) {
                program.set(index, replaced);
                return;
            }
        }
        
        throw new IllegalStateException("Instruction to be replaced not found in program." +
                "\nOriginal instruction: " + original +
                "\nReplacement instruction: " + replaced);
    }
}
