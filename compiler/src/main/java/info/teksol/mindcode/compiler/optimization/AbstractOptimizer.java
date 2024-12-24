package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.MessageLevel;
import info.teksol.mindcode.compiler.GenerationGoal;
import info.teksol.mindcode.compiler.MemoryModel;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.Opcode;
import info.teksol.mindcode.v3.AstContext;
import info.teksol.mindcode.v3.ContextlessInstructionCreator;
import info.teksol.mindcode.v3.MessageConsumer;
import org.intellij.lang.annotations.PrintFormat;

import java.util.List;

abstract class AbstractOptimizer implements Optimizer, ContextlessInstructionCreator {
    protected final Optimization optimization;
    protected final OptimizationContext optimizationContext;
    protected final InstructionProcessor instructionProcessor;
    protected OptimizationLevel level = OptimizationLevel.ADVANCED;
    protected GenerationGoal goal = GenerationGoal.SIZE;
    protected MemoryModel memoryModel = MemoryModel.VOLATILE;
    protected DebugPrinter debugPrinter = new NullDebugPrinter();
    private MessageConsumer messageRecipient = s -> {};

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
    public LogicInstruction createInstruction(AstContext astContext, Opcode opcode, LogicArgument... arguments) {
        return instructionProcessor.createInstruction(astContext, opcode, arguments);
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
    public void setMessageRecipient(MessageConsumer messageRecipient) {
        this.messageRecipient = messageRecipient;
    }

    protected boolean advanced() {
        return level == OptimizationLevel.ADVANCED || level == OptimizationLevel.EXPERIMENTAL;
    }

    protected boolean experimental() {
        return level == OptimizationLevel.EXPERIMENTAL;
    }

    protected void emitMessage(MessageLevel level, @PrintFormat String format, Object... args) {
        messageRecipient.accept(new OptimizerMessage(level, String.format(format, args)));
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
    protected <T extends LogicInstruction> T replaceAllArgs(T instruction, LogicArgument oldArg, LogicArgument newArg) {
        return instructionProcessor.replaceAllArgs(instruction, oldArg, newArg);
    }

    protected <T extends LogicInstruction> T replaceArgs(T instruction, List<LogicArgument> newArgs) {
        return instructionProcessor.replaceArgs(instruction, newArgs);
    }
    //</editor-fold>
}
