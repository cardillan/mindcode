package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.*;
import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.logic.*;
import org.intellij.lang.annotations.PrintFormat;

import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractOptimizer implements Optimizer {
    protected final Optimization optimization;
    protected final OptimizationContext optimizationContext;
    protected final InstructionProcessor instructionProcessor;
    protected OptimizationLevel level = OptimizationLevel.AGGRESSIVE;
    protected GenerationGoal goal = GenerationGoal.SIZE;
    protected MemoryModel memoryModel = MemoryModel.VOLATILE;
    protected DebugPrinter debugPrinter = new NullDebugPrinter();
    private Consumer<CompilerMessage> messageRecipient = s -> {};

    public AbstractOptimizer(Optimization optimization, OptimizationContext optimizationContext) {
        this.optimization = optimization;
        this.optimizationContext = optimizationContext;
        this.instructionProcessor = optimizationContext.getInstructionProcessor();
    }

    AbstractOptimizer(Optimization optimization) {
        this.optimization = optimization;
        this.optimizationContext = null;
        this.instructionProcessor = null;
    }

    @Override
    public String getName() {
        return optimization.getName();
    }

    @Override
    public Optimization getOptimization() {
        return optimization;
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
    public void setMemoryModel(MemoryModel memoryModel) {
        this.memoryModel = memoryModel;
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

    @Override
    public List<OptimizationAction> getPossibleOptimizations(int costLimit) {
        return List.of();
    }

    public void generateFinalMessages() {
    }


    //<editor-fold desc="Inspecting instructions">
    protected boolean isVolatile(LogicInstruction instruction) {
        return instruction.inputArgumentsStream().anyMatch(LogicArgument::isVolatile);
    }
    //</editor-fold>

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

    public GotoOffsetInstruction createGotoOffset(AstContext astContext, LogicLabel target, LogicVariable value, LogicNumber offset, LogicLabel marker) {
        return instructionProcessor.createGotoOffset(astContext, target, value, offset, marker);
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

    public NoOpInstruction createNoOp(AstContext astContext) {
        return instructionProcessor.createNoOp(astContext);
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

    protected <T extends LogicInstruction> T replaceAllArgs(T instruction, LogicArgument oldArg, LogicArgument newArg) {
        return instructionProcessor.replaceAllArgs(instruction, oldArg, newArg);
    }

    protected <T extends LogicInstruction> T replaceArgs(T instruction, List<LogicArgument> newArgs) {
        return instructionProcessor.replaceArgs(instruction, newArgs);
    }
    //</editor-fold>
}
