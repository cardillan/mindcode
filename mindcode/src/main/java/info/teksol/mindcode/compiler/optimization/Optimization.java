package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.InstructionProcessor;

import java.util.List;
import java.util.function.Function;

// The optimizations are applied in the declared order, i.e. ConditionalJumpsNormalizer gets instructions from the
// compiler, makes optimizations and passes them onto the next optimizer.
// TODO: refactor to ensure 1:1 correspondence between enum and implementation. Might need multiple passes.
public enum Optimization {
    CONDITIONAL_JUMPS_NORMALIZATION     ('n', "Jump Normalization",
            ConditionalJumpsNormalizer::new,
            "replacing always true conditional jumps with unconditional ones, removing always false jumps"),

    DEAD_CODE_ELIMINATION               ('d', "Dead Code Elimination",
            DeadCodeEliminator::new,
            "eliminating writes to compiler- or user-defined variables that are not used"),
    
    SINGLE_STEP_JUMP_ELIMINATION        ('s', "Single Step Elimination",
            SingleStepJumpEliminator::new,
            "eliminating jumps to the next instruction"),

    OUTPUT_TEMPS_ELIMINATION            ('o', "Output Temp Elimination",
            OutputTempEliminator::new,
            "eliminating temporary variables created to extract values from instructions"),

    EXPRESSION_OPTIMIZATION             ('x', "Expression Optimization",
            ExpressionOptimizer::new,
            "optimizing some common mathematical expressions"),

    CASE_EXPRESSION_OPTIMIZATION        ('c', "Ease Expression Optimization",
            CaseExpressionOptimizer::new,
            "eliminating temporary variables created to execute case expressions"),
    
    CONDITIONAL_JUMPS_IMPROVEMENT       ('j', "Conditionals Optimization",
            ImproveNegativeConditionalJumps::new,
            "merging an op instruction producing a boolean expression into the following conditional jump"),
    
    JUMP_OVER_JUMP_ELIMINATION          ('q', "Jump Straightening",
            List.of(JumpOverJumpEliminator::new, ImprovePositiveConditionalJumps::new),
            "simplifying sequences of intertwined jumps"),

    LOOP_OPTIMIZATION                   ('l', "Loop Optimization",
            LoopOptimizer::new,
            "improving loops"),

    IF_EXPRESSION_OPTIMIZATION          ('b', "If Expression Optimization",
            IfExpressionOptimizer::new,
            "improving ternary/if expressions"),

    DATA_FLOW_OPTIMIZATION              ('g', "Data Flow Optimization",
            DataFlowOptimizer::new,
            "improving variable assignments and and expressions"),

    JUMP_TARGET_PROPAGATION             ('t', "Jump Threading",
            PropagateJumpTargets::new,
            "eliminating chained jumps"),
    
    // This optimizer can get additional single step jumps; therefore is bundled with its eliminator
    UNREACHABLE_CODE_ELIMINATION       ('e', "Unreachable Code Elimination",
            List.of(UnreachableCodeEliminator::new, SingleStepJumpEliminator::new),
            "eliminating instructions made unreachable by optimizations or false conditions"),

    STACK_USAGE_OPTIMIZATION            ('k', "Stack Optimization",
            StackUsageOptimizer::new,
            "optimizing variable storage on stack"),

    RETURN_VALUE_OPTIMIZATION           ('r', "Return Value Optimization",
            ReturnValueOptimizer::new,
            "optimizing passing return values from functions"),

    INPUT_TEMPS_ELIMINATION             ('i', "Input Temp Elimination",
            InputTempEliminator::new,
            "eliminating temporary variables created to pass values into instructions"),

    PRINT_TEXT_MERGING                  ('p', "Print Merging",
            PrintMerger::new,
            "merging consecutive print statements outputting text literals"),
    ;
    
    private final List<Function<InstructionProcessor, Optimizer>> instanceCreators;
    
    // Command line flag for referencing this optimizer
    private final char flag;
    private final String name;
    private final String optionName;
    private final String description;

    Optimization(char flag, String name, List<Function<InstructionProcessor, Optimizer>> instanceCreators, String description) {
        this.flag = flag;
        this.name = name;
        this.optionName = name.toLowerCase().replace(' ', '-');
        this.instanceCreators = instanceCreators;
        this.description = description;
    }

    Optimization(char flag, String name, Function<InstructionProcessor, Optimizer> instanceCreator, String description) {
        this.flag = flag;
        this.name = name;
        this.optionName = name.toLowerCase().replace(' ', '-');
        this.instanceCreators = List.of(instanceCreator);
        this.description = description;
    }
    
    public char getFlag() {
        return flag;
    }

    public String getName() {
        return name;
    }

    public String getOptionName() {
        return optionName;
    }

    public String getDescription() {
        return description;
    }

    public List<Function<InstructionProcessor, Optimizer>> getInstanceCreators() {
        return instanceCreators;
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
