package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.ArrayList;
import java.util.List;

// Base class for optimizers. Contains helper functions for manipulating instructions.
abstract class BaseOptimizer implements LogicInstructionPipeline {
    protected final LogicInstructionPipeline next;

    public BaseOptimizer(LogicInstructionPipeline next) {
        this.next = next;
    }

    // Creates a new instruction with argument given index set to a new value
    protected LogicInstruction replaceArg(LogicInstruction instruction, int argIndex, String arg) {
        if (instruction.getArgs().get(argIndex).equals(arg)) {
            return instruction;
        }
        else {
            List<String> newArgs = new ArrayList<>(instruction.getArgs());
            newArgs.set(argIndex, arg);
            return new LogicInstruction(instruction.getOpcode(), newArgs);
        }
    }
    
}
