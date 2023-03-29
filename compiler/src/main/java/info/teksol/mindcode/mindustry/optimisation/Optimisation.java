package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

// The optimizations are applied in the declared order, ie. ConditionalJumpsNormalizer gets instructions from the 
// compiler, makes optimizations and passes them onto the next optimizer.
public enum Optimisation {
    CONDITIONAL_JUMPS_NORMALIZATION     ('n', next -> new ConditionalJumpsNormalizer(next),
            "replaces conditional, always false jumps with unconditional ones (improves subsequent optimalisations)"),
    
    DEAD_CODE_ELIMINATION               ('d', next -> new DeadCodeEliminator(next),
            "eliminates writes to compiler or user defined variables that are not used"),
    
    SINGLE_STEP_JUMP_ELIMINATION        ('s', next -> new SingleStepJumpEliminator(next),
            "eliminates jumps to the next instruction"),
    
    INPUT_TEMPS_ELIMINATION             ('i', next -> new InputTempEliminator(next),
            "eliminates temporary variables created to input values into instructions"),
    
    OUTPUT_TEMPS_ELIMINATION            ('o', next -> new OutputTempEliminator(next),
            "eliminates temporary variables created to extract values from instructions"),
    
    CASE_EXPRESSION_OPTIMIZATION        ('c', next -> new CaseExpressionOptimizer(next),
            "eliminates temporary variables created to execute case expressions"),
    
    CONDITIONAL_JUMPS_IMPROVEMENT       ('j', next -> new ImproveConditionalJumps(next),
            "merges a negation of a boolean condition into the following jump"),
    
    PRINT_TEXT_MERGING                  ('p', next -> new PrintMerger(next),
            "merges consecutive print statenents outputting text literals"),
    
    JUMP_TARGET_PROPAGATION             ('t', next -> new PropagateJumpTargets(next),
            "speeds up execution by eliminating chained jumps"),
    
    // This optimizer can create additional single step jumps; therefore is bundled with its eliminator
    INACCESSIBLE_CODE_ELIMINATION       ('e', next -> new InaccesibleCodeEliminator(new SingleStepJumpEliminator(next)),
            "eliminates instructions made inaccessible by optimizations or false conditions"),
    ;
    
    private final Function<LogicInstructionPipeline, ? extends BaseOptimizer> instanceCreator;
    
    // Command line flag for referencing this optimizer
    private final char flag;
    private final String description;

    private Optimisation(char flag, Function<LogicInstructionPipeline, ? extends BaseOptimizer> instanceCreator,
            String description) {
        this.flag = flag;
        this.instanceCreator = instanceCreator;
        this.description = description;
    }
    
    private BaseOptimizer createInstance(LogicInstructionPipeline next) {
        return instanceCreator.apply(next);
    }

    public char getFlag() {
        return flag;
    }

    public String getDescription() {
        return description;
    }
    
    public static Optimisation forFlag(char flag) {
        for (Optimisation o : values()) {
            if (o.getFlag() == flag) {
                return o;
            }
        }
        
        return null;
    }
    
    public static LogicInstructionPipeline createCompletePipeline(LogicInstructionPipeline terminus) {
        return createPipelineOf(terminus, EnumSet.allOf(Optimisation.class), s -> {});
    }
    
    public static LogicInstructionPipeline createPipelineOf(LogicInstructionPipeline terminus,
            Set<Optimisation> optimisations, Consumer<String> messageRecipient) {
        LogicInstructionPipeline pipeline = new InstructionCounter(terminus, messageRecipient,
                "%6d instructions after optimizations.");
        Optimisation[] values = values();
        for (int i = values.length - 1; i >= 0; i--) {
            if (optimisations.contains(values[i])) {
                BaseOptimizer optimizer = values[i].createInstance(pipeline);
                optimizer.setMessagesRecipient(messageRecipient);
                pipeline = optimizer;
            }
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
