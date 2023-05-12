package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;

import java.util.List;
import java.util.function.BiFunction;

// The optimizations are applied in the declared order, i.e. ConditionalJumpsNormalizer gets instructions from the
// compiler, makes optimizations and passes them onto the next optimizer.
public enum Optimization {
    CONDITIONAL_JUMPS_NORMALIZATION     ('n', "jump-normalization",
            ConditionalJumpsNormalizer::new,
            "replacing always true conditional jumps with unconditional ones, removing always false jumps"),

    DEAD_CODE_ELIMINATION               ('d', "dead-code-elimination",
            DeadCodeEliminator::new,
            "eliminating writes to compiler- or user-defined variables that are not used"),
    
    SINGLE_STEP_JUMP_ELIMINATION        ('s', "single-step-elimination",
            SingleStepJumpEliminator::new,
            "eliminating jumps to the next instruction"),
    
    INPUT_TEMPS_ELIMINATION             ('i', "input-temp-elimination",
            InputTempEliminator::new,
            "eliminating temporary variables created to pass values into instructions"),
    
    OUTPUT_TEMPS_ELIMINATION            ('o', "output-temp-elimination",
            OutputTempEliminator::new,
            "eliminating temporary variables created to extract values from instructions"),

    EXPRESSION_OPTIMIZATION             ('x', "expression-optimization",
            ExpressionOptimizer::new,
            "optimizing some common mathematical expressions"),

    CASE_EXPRESSION_OPTIMIZATION        ('c', "case-expression-optimization",
            CaseExpressionOptimizer::new,
            "eliminating temporary variables created to execute case expressions"),
    
    CONDITIONAL_JUMPS_IMPROVEMENT       ('j', "conditionals-optimization",
            ImproveNegativeConditionalJumps::new,
            "merging an op instruction producing a boolean expression into the following conditional jump"),
    
    JUMP_OVER_JUMP_ELIMINATION          ('q', "jump-straightening",
            (inst, next) -> new JumpOverJumpEliminator(inst, new ImprovePositiveConditionalJumps(inst, next)),
            "simplifying sequences of intertwined jumps"),

    JUMP_TARGET_PROPAGATION             ('t', "jump-threading",
            PropagateJumpTargets::new,
            "eliminating chained jumps"),
    
    // This optimizer can get additional single step jumps; therefore is bundled with its eliminator
    INACCESSIBLE_CODE_ELIMINATION       ('e', "inaccessible-code-elimination",
            (inst, next) -> new InaccessibleCodeEliminator(inst, new SingleStepJumpEliminator(inst, next)),
            "eliminating instructions made inaccessible by optimizations or false conditions"),

    STACK_USAGE_OPTIMIZATION            ('k', "stack-optimization",
            StackUsageOptimizer::new,
            "optimizing variable storage on stack"),

    FUNCTION_PARAM_OPTIMIZATION         ('f', "function-call-optimization",
            FunctionParameterOptimizer::new,
            "optimizing passing arguments to functions"),

    RETURN_VALUE_OPTIMIZATION           ('r', "return-value-optimization",
            ReturnValueOptimizer::new,
            "optimizing passing return values from functions"),

    PRINT_TEXT_MERGING                  ('p', "print-merging",
            PrintMerger::new,
            "merging consecutive print statements outputting text literals"),
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
