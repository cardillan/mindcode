package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.CompilerMessage;
import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import info.teksol.mindcode.mindustry.MessageLevel;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import info.teksol.mindcode.mindustry.logic.Opcode;
import java.util.function.Consumer;

// Base class for optimizers. Contains helper functions for manipulating instructions.
abstract class BaseOptimizer implements Optimizer {
    protected final InstructionProcessor instructionProcessor;
    private final LogicInstructionPipeline next;
    private final String name = getClass().getSimpleName();
    protected DebugPrinter debugPrinter = new NullDebugPrinter();
    private Consumer<CompilerMessage> messagesRecipient = s -> {};

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
    public void setMessagesRecipient(Consumer<CompilerMessage> messagesRecipient) {
        this.messagesRecipient = messagesRecipient;
        if (next instanceof Optimizer) {
            ((Optimizer)next).setMessagesRecipient(messagesRecipient);
        }
    }

    protected void emitMessage(MessageLevel level, String format, Object... args) {
        messagesRecipient.accept(new CompilerMessage(level, String.format(format, args)));
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

    public boolean isTemporary(String varRef) {
        return instructionProcessor.isTemporary(varRef);
    }

    protected boolean hasInverse(String comparison) {
        return instructionProcessor.hasInverse(comparison);
}

    protected String getInverse(String comparison) {
        return instructionProcessor.getInverse(comparison);
    }
}
