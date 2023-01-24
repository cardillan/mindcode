package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

// Base class for optimizers. Contains helper functions for manipulating instructions.
abstract class BaseOptimizer implements Optimizer {
    private static final Map<String, String> INVERSES = Map.of(
            "equal", "notEqual",
            "notEqual", "equal",
            "lessThan", "greaterThanEq",
            "lessThanEq", "greaterThan",
            "greaterThan", "lessThanEq",
            "greaterThanEq", "lessThan"
    );

    private final LogicInstructionPipeline next;
    private final String name = getClass().getSimpleName();
    protected DebugPrinter debugPrinter = new NullDebugPrinter();
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setDebugPrinter(DebugPrinter debugPrinter) {
        this.debugPrinter = debugPrinter;
        if (next instanceof Optimizer) {
            ((Optimizer)next).setDebugPrinter(debugPrinter);
        }
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

    protected boolean hasInverse(String comparison) {
        return INVERSES.containsKey(comparison);
    }

    protected String getInverse(String comparison) {
        return INVERSES.get(comparison);
    }
}
