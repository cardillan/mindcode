package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Function;

// The optimizations are applied in the declared order, ie. ConditionalJumpsNormalizer gets instructions from the 
// compiler, makes optimizations and passes them onto the next optimizer.
public enum Optimisation {
    CONDITIONAL_JUMPS_NORMALIZATION     (next -> new ConditionalJumpsNormalizer(next)),
    DEAD_CODE_ELIMINATION               (next -> new DeadCodeEliminator(next)),
    SINGLE_STEP_JUMP_ELIMINATION        (next -> new SingleStepJumpEliminator(next)),
    INPUT_TEMPS_ELIMINATION             (next -> new InputTempEliminator(next)),
    OUTPUT_TEMPS_ELIMINATION            (next -> new OutputTempEliminator(next)),
    CASE_EXPRESSION_OPTIMIZATION        (next -> new CaseExpressionOptimizer(next)),
    CONDITIONAL_JUMPS_IMPROVEMENT       (next -> new ImproveConditionalJumps(next)),
    JUMP_TARGET_PROPAGATION             (next -> new PropagateJumpTargets(next)),
    // This optimizer can create additional single step jumps; therefore is bundled with its eliminator
    INACCESSIBLE_CODE_ELIMINATION       (next -> new InaccesibleCodeEliminator(new SingleStepJumpEliminator(next))),
    ;
    
    private final Function<LogicInstructionPipeline, ? extends BaseOptimizer> instanceCreator;

    private Optimisation(Function<LogicInstructionPipeline, ? extends BaseOptimizer> instanceCreator) {
        this.instanceCreator = instanceCreator;
    }
    
    private BaseOptimizer createInstance(LogicInstructionPipeline next) {
        return instanceCreator.apply(next);
    }
    
    public static LogicInstructionPipeline createCompletePipeline(LogicInstructionPipeline terminus) {
        return createCompletePipeline(terminus, s -> {});
    }
    
    public static LogicInstructionPipeline createCompletePipeline(LogicInstructionPipeline terminus,
            Consumer<String> messageRecipient) {
        LogicInstructionPipeline pipeline = new InstructionCounter(terminus, messageRecipient,
                "%6d instructions after optimizations.");
        Optimisation[] values = values();
        for (int i = values.length - 1; i >= 0; i--) {
            BaseOptimizer optimizer = values[i].createInstance(pipeline);
            optimizer.setMessagesRecipient(messageRecipient);
            pipeline = optimizer;
        }
        return new InstructionCounter(pipeline, messageRecipient, "%6d instructions before optimizations.");
    }
    
    public static LogicInstructionPipeline createPipelineOf(LogicInstructionPipeline terminus, Optimisation... optimisation) {
        EnumSet<Optimisation> includes = EnumSet.of(optimisation[0], optimisation);
        LogicInstructionPipeline pipeline = terminus;
        Optimisation[] values = values();
        for (int i = values.length - 1; i >= 0; i--) {
            if (includes.contains(values[i])) {
                pipeline = values[i].createInstance(pipeline);
            }
        }
        return pipeline;
    }
    
    private static class InstructionCounter implements LogicInstructionPipeline {
        private final LogicInstructionPipeline next;
        private final Consumer<String> messageRecipient;
        private final String message;
        private int count = 0;

        public InstructionCounter(LogicInstructionPipeline next, Consumer<String> messageRecipient, String message) {
            this.next = next;
            this.messageRecipient = messageRecipient;
            this.message = message;
        }

        @Override
        public void emit(LogicInstruction instruction) {
            if (!instruction.isLabel()) {
                count++;
            }
            next.emit(instruction);
        }

        @Override
        public void flush() {
            messageRecipient.accept(String.format(message, count));
            next.flush();
        }
    }
}
