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

    // Creates a new instruction with argument at given index set to a new value
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
    
    // Creates a new instruction with all occurences of oldArg replaced newArg in the argument list
    protected LogicInstruction replaceAllArgs(LogicInstruction instruction, String oldArg, String newArg) {
        List<String> args = new ArrayList<>(instruction.getArgs());
        args.replaceAll(arg -> arg.equals(oldArg) ? newArg : arg);
        return new LogicInstruction(instruction.getOpcode(), args);
    }
}
