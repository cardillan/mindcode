package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.GenerationGoal;
import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.MindcodeMessage;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.logic.Condition;
import info.teksol.mindcode.logic.LogicAddress;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Operation;
import org.intellij.lang.annotations.PrintFormat;

import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractOptimizer implements Optimizer {
    protected final InstructionProcessor instructionProcessor;
    private final String name = getClass().getSimpleName();  // TODO: use name from Optimization enum

    protected OptimizationLevel level = OptimizationLevel.AGGRESSIVE;

    protected GenerationGoal goal  = GenerationGoal.SIZE;
    protected DebugPrinter debugPrinter = new NullDebugPrinter();
    private Consumer<CompilerMessage> messageRecipient = s -> {};

    public AbstractOptimizer(InstructionProcessor instructionProcessor) {
        this.instructionProcessor = instructionProcessor;
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
    public void setGoal(GenerationGoal goal) {
        this.goal = goal;
    }

    @Override
    public void setDebugPrinter(DebugPrinter debugPrinter) {
        this.debugPrinter = debugPrinter;
    }

    @Override
    public void setMessageRecipient(Consumer<CompilerMessage> messageRecipient) {
        this.messageRecipient = messageRecipient;
    }

    protected boolean aggressive() {
        return level == OptimizationLevel.AGGRESSIVE;
    }

    protected void emitMessage(MessageLevel level, @PrintFormat String format, Object... args) {
        messageRecipient.accept(new MindcodeMessage(level, String.format(format, args)));
    }

    //<editor-fold desc="Instruction creation">
    protected CallInstruction createCallStackless(AstContext astContext, LogicAddress address) {
        return instructionProcessor.createCallStackless(astContext, address);
    }

    protected CallRecInstruction createCallRecursive(AstContext astContext, LogicVariable stack, LogicLabel callAddr, LogicLabel retAddr) {
        return instructionProcessor.createCallRecursive(astContext, stack, callAddr, retAddr);
    }

    protected EndInstruction createEnd(AstContext astContext) {
        return instructionProcessor.createEnd(astContext);
    }

    protected GotoInstruction createGoto(AstContext astContext, LogicVariable address, LogicLabel marker) {
        return instructionProcessor.createGoto(astContext, address, marker);
    }

    protected JumpInstruction createJump(AstContext astContext, LogicLabel target, Condition condition, LogicValue x, LogicValue y) {
        return instructionProcessor.createJump(astContext, target, condition, x, y);
    }

    protected JumpInstruction createJumpUnconditional(AstContext astContext, LogicLabel target) {
        return instructionProcessor.createJumpUnconditional(astContext, target);
    }

    protected LabelInstruction createLabel(AstContext astContext, LogicLabel label) {
        return instructionProcessor.createLabel(astContext, label);
    }

    public GotoLabelInstruction createGotoLabel(AstContext astContext, LogicLabel label, LogicLabel marker) {
        return instructionProcessor.createGotoLabel(astContext, label, marker);
    }

    protected OpInstruction createOp(AstContext astContext, Operation operation, LogicVariable target, LogicValue first) {
        return instructionProcessor.createOp(astContext, operation, target, first);
    }

    protected OpInstruction createOp(AstContext astContext, Operation operation, LogicVariable target, LogicValue first, LogicValue second) {
        return instructionProcessor.createOp(astContext, operation, target, first, second);
    }

    protected PopInstruction createPop(AstContext astContext, LogicVariable memory, LogicVariable value) {
        return instructionProcessor.createPop(astContext, memory, value);
    }

    protected PrintInstruction createPrint(AstContext astContext, LogicValue what) {
        return instructionProcessor.createPrint(astContext, what);
    }

    protected PrintflushInstruction createPrintflush(AstContext astContext, LogicVariable messageBlock) {
        return instructionProcessor.createPrintflush(astContext, messageBlock);
    }

    protected PushInstruction createPush(AstContext astContext, LogicVariable memory, LogicVariable value) {
        return instructionProcessor.createPush(astContext, memory, value);
    }

    protected ReadInstruction createRead(AstContext astContext, LogicVariable result, LogicVariable memory, LogicValue index) {
        return instructionProcessor.createRead(astContext, result, memory, index);
    }

    protected ReturnInstruction createReturn(AstContext astContext, LogicVariable stack) {
        return instructionProcessor.createReturn(astContext, stack);
    }

    protected SensorInstruction createSensor(AstContext astContext, LogicVariable result, LogicValue target, LogicValue property) {
        return instructionProcessor.createSensor(astContext, result, target, property);
    }

    protected SetInstruction createSet(AstContext astContext, LogicVariable target, LogicValue value) {
        return instructionProcessor.createSet(astContext, target, value);
    }

    protected SetAddressInstruction createSetAddress(AstContext astContext, LogicVariable variable, LogicLabel address) {
        return instructionProcessor.createSetAddress(astContext, variable, address);
    }

    protected StopInstruction createStop(AstContext astContext) {
        return instructionProcessor.createStop(astContext);
    }

    protected WriteInstruction createWrite(AstContext astContext, LogicValue value, LogicVariable memory, LogicValue index) {
        return instructionProcessor.createWrite(astContext, value, memory, index);
    }

    protected LogicInstruction replaceAllArgs(LogicInstruction instruction, LogicArgument oldArg, LogicArgument newArg) {
        return instructionProcessor.replaceAllArgs(instruction, oldArg, newArg);
    }

    protected LogicInstruction replaceArgs(LogicInstruction instruction, List<LogicArgument> newArgs) {
        return instructionProcessor.replaceArgs(instruction, newArgs);
    }
    //</editor-fold>
}
