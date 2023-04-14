package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;

import java.util.List;
import java.util.function.BiFunction;

// The optimizations are applied in the declared order, i.e. ConditionalJumpsNormalizer gets instructions from the
// compiler, makes optimizations and passes them onto the next optimizer.
public enum Optimization {
    CONDITIONAL_JUMPS_NORMALIZATION     ('n', "jumpNormalization",
            ConditionalJumpsNormalizer::new,
            "replaces always true conditional jumps with unconditional ones, removes always false jumps"),

    DEAD_CODE_ELIMINATION               ('d', "deadCodeElimination",
            DeadCodeEliminator::new,
            "eliminates writes to compiler or user defined variables that are not used"),
    
    SINGLE_STEP_JUMP_ELIMINATION        ('s', "singleStepElimination",
            SingleStepJumpEliminator::new,
            "eliminates jumps to the next instruction"),
    
    INPUT_TEMPS_ELIMINATION             ('i', "inputTempElimination",
            InputTempEliminator::new,
            "eliminates temporary variables created to input values into instructions"),
    
    OUTPUT_TEMPS_ELIMINATION            ('o', "outputTempElimination",
            OutputTempEliminator::new,
            "eliminates temporary variables created to extract values from instructions"),

    EXPRESSION_OPTIMIZATION             ('x', "expressionOptimization",
            ExpressionOptimizer::new,
            "optimizes some common mathematical expressions"),

    CASE_EXPRESSION_OPTIMIZATION        ('c', "caseExpressionOptimization",
            CaseExpressionOptimizer::new,
            "eliminates temporary variables created to execute case expressions"),
    
    CONDITIONAL_JUMPS_IMPROVEMENT       ('j', "conditionalsOptimization",
            ImproveNegativeConditionalJumps::new,
            "merges an op instruction producing a boolean expression into the following conditional jump"),
    
    JUMP_OVER_JUMP_ELIMINATION          ('q', "jumpStraightening",
            (inst, next) -> new JumpOverJumpEliminator(inst, new ImprovePositiveConditionalJumps(inst, next)),
            "simplifies sequences of intertwined jumps"),

    JUMP_TARGET_PROPAGATION             ('t', "jumpThreading",
            PropagateJumpTargets::new,
            "speeds up execution by eliminating chained jumps"),
    
    // This optimizer can create additional single step jumps; therefore is bundled with its eliminator
    INACCESSIBLE_CODE_ELIMINATION       ('e', "inaccessibleCodeElimination",
            (inst, next) -> new InaccessibleCodeEliminator(inst, new SingleStepJumpEliminator(inst, next)),
            "eliminates instructions made inaccessible by optimizations or false conditions"),

    STACK_USAGE_OPTIMIZATION            ('k', "stackOptimization",
            StackUsageOptimizer::new,
            "optimizes variable storage on stack"),

    FUNCTION_PARAM_OPTIMIZATION         ('f', "functionCallOptimization",
            FunctionParameterOptimizer::new,
            "optimizes passing arguments to functions"),

    PRINT_TEXT_MERGING                  ('p', "printMerging",
            PrintMerger::new,
            "merges consecutive print statements outputting text literals"),
    ;
    
    private final BiFunction<InstructionProcessor, LogicInstructionPipeline, ? extends BaseOptimizer> instanceCreator;
    
    // Command line flag for referencing this optimizer
    private final char flag;
    private final String name;
    private final String description;

    Optimization(char flag, String name,
                 BiFunction<InstructionProcessor, LogicInstructionPipeline, ? extends BaseOptimizer> instanceCreator,
                 String description) {
        this.flag = flag;
        this.name = name;
        this.instanceCreator = instanceCreator;
        this.description = description;
    }
    
    public char getFlag() {
        return flag;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    
    BaseOptimizer createInstance(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        return instanceCreator.apply(instructionProcessor, next);
    }

    public static final List<Optimization> LIST = List.of(values());

    public static Optimization forFlag(char flag) {
        for (Optimization o : values()) {
            if (o.getFlag() == flag) {
                return o;
            }
        }
        
        return null;
    }
}
