package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.instructions.ContextlessInstructionCreator;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GenerationGoal;
import info.teksol.mc.profile.GlobalCompilerProfile;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
abstract class AbstractOptimizer extends AbstractMessageEmitter implements Optimizer, ContextlessInstructionCreator {
    protected final Optimization optimization;
    protected final OptimizationContext optimizationContext;
    protected final InstructionProcessor instructionProcessor;
    protected final MindustryMetadata metadata;
    protected final boolean debugOutput;
    protected OptimizationLevel level = OptimizationLevel.EXPERIMENTAL;
    protected DebugPrinter debugPrinter = new NullDebugPrinter();

    public AbstractOptimizer(Optimization optimization, OptimizationContext optimizationContext) {
        super(optimizationContext.getMessageConsumer());
        this.optimization = optimization;
        this.optimizationContext = optimizationContext;
        this.instructionProcessor = optimizationContext.getInstructionProcessor();
        this.metadata = instructionProcessor.getMetadata();
        this.debugOutput = getGlobalProfile().isDebugOutput();
    }

    @Override
    public LogicInstruction createInstruction(AstContext astContext, Opcode opcode, List<LogicArgument> arguments) {
        return instructionProcessor.createInstruction(astContext, opcode, arguments);
    }

    @Override
    public InstructionProcessor getProcessor() {
        return instructionProcessor;
    }

    @Override
    public String getName() {
        return optimization.getName();
    }

    public GlobalCompilerProfile getGlobalProfile() {
        return optimizationContext.getGlobalProfile();
    }

    public CompilerProfile getCompilerProfile() {
        return optimizationContext.getGlobalProfile();
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
    public void setDebugPrinter(DebugPrinter debugPrinter) {
        this.debugPrinter = debugPrinter;
    }

    protected void debugOutput(Object message) {
        if (isDebugOutput()) System.out.println(message);
    }

    protected void debugOutput(@PrintFormat String format, Object... args) {
        if (isDebugOutput()) {
            System.out.printf(format, args);
            System.out.println();
        }
    }

    protected boolean isDebugOutput() {
        return debugOutput;
    }

    protected boolean advanced(AstContext context) {
        OptimizationLevel level = context.getLocalProfile().getOptimizationLevel(optimization);
        return level == OptimizationLevel.ADVANCED || level == OptimizationLevel.EXPERIMENTAL;
    }

    protected boolean advanced(LogicInstruction instruction) {
        return advanced(instruction.getAstContext());
    }

    protected boolean experimental(AstContext context) {
        OptimizationLevel level = context.getLocalProfile().getOptimizationLevel(optimization);
        return level == OptimizationLevel.EXPERIMENTAL;
    }

    protected boolean experimental(LogicInstruction instruction) {
        return experimental(instruction.getAstContext());
    }

    protected boolean experimentalGlobal() {
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

    @NullMarked
    protected abstract class AbstractOptimizationAction implements OptimizationAction {
        protected final GenerationGoal goal;
        protected final AstContext astContext;
        protected final int cost;
        protected final double benefit;

        public AbstractOptimizationAction(AstContext astContext, int cost, double benefit) {
            this.goal = astContext.getLocalProfile().getGoal();
            this.astContext = astContext;
            this.cost = cost;
            this.benefit = benefit;
        }

        @Override
        public GenerationGoal goal() {
            return goal;
        }

        @Override
        public Optimization optimization() {
            return optimization;
        }

        @Override
        public AstContext astContext() {
            return astContext;
        }

        @Override
        public int cost() {
            return cost;
        }

        @Override
        public double benefit() {
            return benefit;
        }

        public abstract String toString();
    }
}
