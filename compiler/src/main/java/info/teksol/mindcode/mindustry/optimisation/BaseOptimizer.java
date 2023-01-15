package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// Base class for optimizers. Contains helper functions for manipulating instructions.
abstract class BaseOptimizer implements LogicInstructionPipeline {
    private final LogicInstructionPipeline next;
    private Consumer<String> messagesRecipient = s -> {};

    public BaseOptimizer(LogicInstructionPipeline next) {
        this.next = next;
    }
    
    protected void emitToNext(LogicInstruction instruction) {
        next.emit(instruction);
    }

    @Override
    public void flush() {
        next.flush();
    }

    void setMessagesRecipient(Consumer<String> messagesRecipient) {
        this.messagesRecipient = messagesRecipient;
    }

    protected void emitMessage(String fornat, Object... args) {
        messagesRecipient.accept(String.format(fornat, args));
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
