package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import java.util.function.BiFunction;

// The optimizations are applied in the declared order, ie. ConditionalJumpsNormalizer gets instructions from the 
// compiler, makes optimizations and passes them onto the next optimizer.
public enum Optimisation {
    CONDITIONAL_JUMPS_NORMALIZATION     ('n', (inst, next) -> new ConditionalJumpsNormalizer(inst, next),
            "replaces always true conditional jumps with unconditional ones, removes always false jumps"),

    DEAD_CODE_ELIMINATION               ('d', (inst, next) -> new DeadCodeEliminator(inst, next),
            "eliminates writes to compiler or user defined variables that are not used"),
    
    SINGLE_STEP_JUMP_ELIMINATION        ('s', (inst, next) -> new SingleStepJumpEliminator(inst, next),
            "eliminates jumps to the next instruction"),
    
    INPUT_TEMPS_ELIMINATION             ('i', (inst, next) -> new InputTempEliminator(inst, next),
            "eliminates temporary variables created to input values into instructions"),
    
    OUTPUT_TEMPS_ELIMINATION            ('o', (inst, next) -> new OutputTempEliminator(inst, next),
            "eliminates temporary variables created to extract values from instructions"),
    
    CASE_EXPRESSION_OPTIMIZATION        ('c', (inst, next) -> new CaseExpressionOptimizer(inst, next),
            "eliminates temporary variables created to execute case expressions"),
    
    CONDITIONAL_JUMPS_IMPROVEMENT       ('j', (inst, next) -> new ImproveNegativeConditionalJumps(inst, next),
            "merges an op instruction producing a boolean expression into the following conditional jump"),
    
    JUMP_OVER_JUMP_ELIMINATION          ('q',
            (inst, next) -> new JumpOverJumpEliminator(inst, new ImprovePositiveConditionalJumps(inst, next)),
            "simplifies sequences of intertwined jumps"),

    JUMP_TARGET_PROPAGATION             ('t', (inst, next) -> new PropagateJumpTargets(inst, next),
            "speeds up execution by eliminating chained jumps"),
    
    // This optimizer can create additional single step jumps; therefore is bundled with its eliminator
    INACCESSIBLE_CODE_ELIMINATION       ('e',
            (inst, next) -> new InaccesibleCodeEliminator(inst, new SingleStepJumpEliminator(inst, next)),
            "eliminates instructions made inaccessible by optimizations or false conditions"),

    STACK_USAGE_OPTIMIZATION            ('k', (inst, next) -> new StackUsageOptimizer(inst, next),
            "optimizes variable storage on stack"),

    FUNCTION_PARAM_OPTIMIZATION         ('f', (inst, next) -> new FunctionParameterOptimizer(inst, next),
            "optimizes passing arguments to functions"),

    PRINT_TEXT_MERGING                  ('p', (inst, next) -> new PrintMerger(inst, next),
            "merges consecutive print statenents outputting text literals"),
    ;
    
    private final BiFunction<InstructionProcessor, LogicInstructionPipeline, ? extends BaseOptimizer> instanceCreator;
    
    // Command line flag for referencing this optimizer
    private final char flag;
    private final String description;

    private Optimisation(char flag, BiFunction<InstructionProcessor, LogicInstructionPipeline, ? extends BaseOptimizer>
            instanceCreator, String description) {
        this.flag = flag;
        this.instanceCreator = instanceCreator;
        this.description = description;
    }
    
    public char getFlag() {
        return flag;
    }

    public String getDescription() {
        return description;
    }
    
    BaseOptimizer createInstance(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        return instanceCreator.apply(instructionProcessor, next);
    }

    public static Optimisation forFlag(char flag) {
        for (Optimisation o : values()) {
            if (o.getFlag() == flag) {
                return o;
            }
        }
        
        return null;
    }
}
