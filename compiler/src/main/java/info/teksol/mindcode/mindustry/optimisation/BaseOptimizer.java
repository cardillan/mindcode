package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import info.teksol.mindcode.mindustry.logic.Opcode;
import java.util.Map;
import java.util.function.Consumer;

// Base class for optimizers. Contains helper functions for manipulating instructions.
abstract class BaseOptimizer implements Optimizer {
    // TODO: move to instructionProcessor
    private static final Map<String, String> INVERSES = Map.of(
            "equal", "notEqual",
            "notEqual", "equal",
            "lessThan", "greaterThanEq",
            "lessThanEq", "greaterThan",
            "greaterThan", "lessThanEq",
            "greaterThanEq", "lessThan"
    );

    protected final InstructionProcessor instructionProcessor;
    private final LogicInstructionPipeline next;
    private final String name = getClass().getSimpleName();
    protected DebugPrinter debugPrinter = new NullDebugPrinter();
    private Consumer<String> messagesRecipient = s -> {};

    public BaseOptimizer(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        this.instructionProcessor = instructionProcessor;
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

    @Override
    public void setMessagesRecipient(Consumer<String> messagesRecipient) {
        this.messagesRecipient = messagesRecipient;
        if (next instanceof Optimizer) {
            ((Optimizer)next).setMessagesRecipient(messagesRecipient);
        }
    }

    protected void emitMessage(String fornat, Object... args) {
        messagesRecipient.accept(String.format(fornat, args));
    }
    
    protected final LogicInstruction createInstruction(Opcode opcode, String... args) {
        return instructionProcessor.createInstruction(opcode, args);
    }

    protected LogicInstruction replaceArg(LogicInstruction instruction, int argIndex, String value) {
        return instructionProcessor.replaceArg(instruction, argIndex, value);
    }
    
    protected LogicInstruction replaceAllArgs(LogicInstruction instruction, String oldArg, String newArg) {
        return instructionProcessor.replaceAllArgs(instruction, oldArg, newArg);
    }

    protected boolean hasInverse(String comparison) {
        return INVERSES.containsKey(comparison);
    }

    protected String getInverse(String comparison) {
        return INVERSES.get(comparison);
    }
}
