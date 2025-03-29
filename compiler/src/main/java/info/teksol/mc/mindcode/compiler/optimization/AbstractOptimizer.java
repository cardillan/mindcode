package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.instructions.ContextlessInstructionCreator;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GenerationGoal;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
abstract class AbstractOptimizer extends AbstractMessageEmitter implements Optimizer, ContextlessInstructionCreator {
    protected final Optimization optimization;
    protected final OptimizationContext optimizationContext;
    protected final InstructionProcessor instructionProcessor;
    protected OptimizationLevel level = OptimizationLevel.EXPERIMENTAL;
    protected GenerationGoal goal = GenerationGoal.SIZE;
    protected DebugPrinter debugPrinter = new NullDebugPrinter();

    public AbstractOptimizer(Optimization optimization, OptimizationContext optimizationContext) {
        super(optimizationContext.getMessageConsumer());
        this.optimization = optimization;
        this.optimizationContext = optimizationContext;
        this.instructionProcessor = optimizationContext.getInstructionProcessor();
    }

    @Override
    public LogicInstruction createInstruction(AstContext astContext, Opcode opcode, List<LogicArgument> arguments) {
        return instructionProcessor.createInstruction(astContext, opcode, arguments);
    }

    @Override
    public String getName() {
        return optimization.getName();
    }

    public CompilerProfile getProfile() {
        return optimizationContext.getProfile();
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
    public void setDebugPrinter(DebugPrinter debugPrinter) {
        this.debugPrinter = debugPrinter;
    }

    protected boolean advanced() {
        return level == OptimizationLevel.ADVANCED || level == OptimizationLevel.EXPERIMENTAL;
    }

    protected boolean experimental() {
        return level == OptimizationLevel.EXPERIMENTAL;
    }

    protected void emitMessage(MessageLevel level, @PrintFormat String format, Object... args) {
        addMessage(new OptimizerMessage(level, String.format(format, args)));
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
