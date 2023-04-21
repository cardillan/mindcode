package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.JumpInstruction;
import info.teksol.mindcode.compiler.instructions.LabelInstruction;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.*;

import java.util.function.Consumer;

// Base class for optimizers. Contains helper functions for manipulating instructions.
abstract class BaseOptimizer implements Optimizer {
    protected final InstructionProcessor instructionProcessor;
    private final LogicInstructionPipeline next;
    private final String name = getClass().getSimpleName();  // TODO: use name from Optimization enum

    protected OptimizationLevel level = OptimizationLevel.AGGRESSIVE;
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
    public void setLevel(OptimizationLevel level) {
        this.level = level;
    }

    @Override
    public void setDebugPrinter(DebugPrinter debugPrinter) {
        this.debugPrinter = debugPrinter;
        if (next instanceof Optimizer optimizer) {
            optimizer.setDebugPrinter(debugPrinter);
        }
    }

    @Override
    public void setMessagesRecipient(Consumer<CompilerMessage> messagesRecipient) {
        this.messagesRecipient = messagesRecipient;
        if (next instanceof Optimizer optimizer) {
            optimizer.setMessagesRecipient(messagesRecipient);
        }
    }

    protected void emitMessage(MessageLevel level, String format, Object... args) {
        messagesRecipient.accept(new CompilerMessage(level, String.format(format, args)));
    }

    public JumpInstruction createUnconditionalJump(LogicLabel label) {
        return instructionProcessor.createJumpUnconditional(label);
    }

    public JumpInstruction createJump(LogicLabel label, Condition condition, LogicValue x, LogicValue y) {
        return instructionProcessor.createJump(label, condition, x, y);
    }

    public LabelInstruction createLabel(LogicLabel label) {
        return instructionProcessor.createLabel(label);
    }

    protected final LogicInstruction createInstruction(Opcode opcode, LogicArgument... args) {
        return instructionProcessor.createInstruction(opcode, args);
    }

    protected LogicInstruction replaceAllArgs(LogicInstruction instruction, LogicArgument oldArg, LogicArgument newArg) {
        return instructionProcessor.replaceAllArgs(instruction, oldArg, newArg);
    }
}
