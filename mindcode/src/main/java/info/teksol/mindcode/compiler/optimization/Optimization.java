package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.InstructionProcessor;

import java.util.List;
import java.util.function.Function;

// The optimizations are applied in the declared order, i.e. ConditionalJumpsNormalizer gets instructions from the
// compiler, makes optimizations and passes them onto the next optimizer.
public enum Optimization {
    TMP_VARIABLES_ELIMINATION           ("Tmp Variables Elimination",
            TempVariableEliminator::new,
            "eliminating temporary variables created to extract values from instructions"),

    CASE_EXPRESSION_OPTIMIZATION        ("Case Expression Optimization",
            CaseExpressionOptimizer::new,
            "eliminating temporary variables created to execute case expressions"),

    CONDITIONAL_JUMPS_NORMALIZATION     ("Jump Normalization",
            ConditionalJumpsNormalizer::new,
            "replacing always true conditional jumps with unconditional ones, removing always false jumps"),

    DEAD_CODE_ELIMINATION               ("Dead Code Elimination",
            DeadCodeEliminator::new,
            "eliminating writes to compiler- or user-defined variables that are not used"),

    CONDITIONAL_JUMPS_IMPROVEMENT       ("Conditional Jump Optimization",
            ImproveConditionalJumps::new,
            "merging an op instruction producing a boolean expression into the following conditional jump"),

    SINGLE_STEP_JUMP_ELIMINATION        ("Single Step Elimination",
            SingleStepJumpEliminator::new,
            "eliminating jumps to the next instruction"),

    EXPRESSION_OPTIMIZATION             ("Expression Optimization",
            ExpressionOptimizer::new,
            "optimizing some common mathematical expressions"),

    LOOP_OPTIMIZATION                   ("Loop Optimization",
            LoopOptimizer::new,
            "improving loops"),

    IF_EXPRESSION_OPTIMIZATION          ("If Expression Optimization",
            IfExpressionOptimizer::new,
            "improving ternary/if expressions"),

    DATA_FLOW_OPTIMIZATION              ("Data Flow Optimization",
            DataFlowOptimizer::new,
            "improving variable assignments and and expressions"),

    UNREACHABLE_CODE_ELIMINATION       ("Unreachable Code Elimination",
            UnreachableCodeEliminator::new,
            "eliminating instructions made unreachable by optimizations or false conditions"),

    STACK_USAGE_OPTIMIZATION            ("Stack Optimization",
            StackUsageOptimizer::new,
            "optimizing variable storage on stack"),

    JUMP_OVER_JUMP_ELIMINATION          ("Jump Straightening",
            JumpOverJumpEliminator::new,
            "simplifying sequences of intertwined jumps"),

    JUMP_TARGET_PROPAGATION             ("Jump Threading",
            PropagateJumpTargets::new,
            "eliminating chained jumps"),

    PRINT_TEXT_MERGING                  ("Print Merging",
            PrintMerger::new,
            "merging consecutive print statements outputting text literals"),
    ;

    private final String name;
    private final Function<InstructionProcessor, Optimizer> instanceCreator;
    private final String optionName;
    private final String description;

    Optimization(String name, Function<InstructionProcessor, Optimizer> instanceCreator, String description) {
        this.name = name;
        this.optionName = name.toLowerCase().replace(' ', '-');
        this.instanceCreator = instanceCreator;
        this.description = description;
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

    public Function<InstructionProcessor, Optimizer> getInstanceCreator() {
        return instanceCreator;
    }

    public static final List<Optimization> LIST = List.of(values());
}
