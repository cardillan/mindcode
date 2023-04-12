package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LabelInstruction;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


// Base class for global optimizers. Consumes the entire program before performing optimizations on it.
// Contains helper method to navigate and manipulate the program.
abstract class GlobalOptimizer extends BaseOptimizer {
    protected final List<LogicInstruction> program = new ArrayList<>();
    private int iterations = 0;
    private int original;
    private int emitted;
    
    public GlobalOptimizer(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    @Override
    public final void emit(LogicInstruction instruction) {
        program.add(instruction);
    }

    @Override
    public final void flush() {
        original = (int) program.stream().filter(ix -> !(ix instanceof LabelInstruction)).count();

        int size;
        boolean repeat;
        do {
            size = program.size();
            repeat = optimizeProgram();
            debugPrinter.iterationFinished(this, ++iterations, program);
        } while (repeat && size != program.size());

        emitted = (int) program.stream().filter(ix -> !(ix instanceof LabelInstruction)).count();

        generateFinalMessages();
        
        program.forEach(this::emitToNext);
        program.clear();
        super.flush();
    }

    /**
     * Performs one iteration of the optimization. Return true to run another iteration, false when done.
     * @return true to re-run the optimization
     */
    protected abstract boolean optimizeProgram();

    protected void generateFinalMessages() {
        if (emitted != original) {
            if (iterations > 1) {
                emitMessage(MessageLevel.INFO, "%6d instructions eliminated by %s (%d iterations).", original - emitted,
                        getClass().getSimpleName(), iterations - 1);
            } else {
                emitMessage(MessageLevel.INFO, "%6d instructions eliminated by %s.", original - emitted, getClass().getSimpleName());
            }
        }
    }

    // Starting at given index, finds first instruction matching predicate.
    // Returns the index or -1 if not found.
    protected int findInstructionIndex(int startIndex, Predicate<LogicInstruction> matcher) {
        for (int i = startIndex; i < program.size(); i++) {
            LogicInstruction instruction = program.get(i);
            if (matcher.test(instruction)) {
                return i;
            }
        }
        
        return -1;
    }
    
    // Starting at given index, find first instruction matching predicate. Return null if not found.
    protected LogicInstruction findInstruction(int startIndex, Predicate<LogicInstruction> matcher) {
        int result = findInstructionIndex(startIndex, matcher);
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
        
        throw new OptimizationException("Instruction to be replaced not found in program." +
                "\nOriginal instruction: " + original +
                "\nReplacement instruction: " + replaced);
    }

    protected void removeInstruction(LogicInstruction original) {
        for (int index = 0; index < program.size(); index++) {
            if (program.get(index) == original) {
                program.remove(index);
                return;
            }
        }

        throw new OptimizationException("Instruction to be removed not found in program." +
                "\nInstruction: " + original);
    }
}
