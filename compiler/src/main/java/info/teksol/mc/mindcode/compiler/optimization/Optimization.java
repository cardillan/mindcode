package info.teksol.mc.mindcode.compiler.optimization;

import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

// The optimizations are applied in the declared order, i.e. ConditionalJumpsNormalizer gets instructions from the
// compiler, makes optimizations and passes them onto the next optimizer.
@NullMarked
public enum Optimization {
    TEMP_VARIABLES_ELIMINATION("Temp Variables Elimination",
            TempVariableEliminator::new,
            "eliminating temporary variables created to extract values from instructions"),

    CASE_EXPRESSION_OPTIMIZATION("Case Expression Optimization",
            CaseExpressionOptimizer::new,
            "eliminating temporary variables created to execute case expressions"),

    DEAD_CODE_ELIMINATION("Dead Code Elimination",
            DeadCodeEliminator::new,
            "eliminating writes to compiler- or user-defined variables that are not used"),

    JUMP_NORMALIZATION("Jump Normalization",
            JumpNormalizer::new,
            "replacing always true conditional jumps with unconditional ones, removing always false jumps"),

    CONDITION_OPTIMIZATION("Condition Optimization",
            ConditionOptimizer::new,
            "merging an op instruction producing a boolean expression into the following conditional jump"),

    SINGLE_STEP_ELIMINATION("Single Step Elimination",
            SingleStepEliminator::new,
            "eliminating jumps to the next instruction"),

    EXPRESSION_OPTIMIZATION("Expression Optimization",
            ExpressionOptimizer::new,
            "optimizing some common mathematical expressions"),

    BOOLEAN_OPTIMIZATION("Boolean Optimization",
            BooleanOptimizer::new,
            "simplifying boolean expressions and/or implementing them using the 'select' instruction"),

    IF_EXPRESSION_OPTIMIZATION("If Expression Optimization",
            IfExpressionOptimizer::new,
            "improving ternary/if expressions"),

    DATA_FLOW_OPTIMIZATION("Data Flow Optimization",
            DataFlowOptimizer::new,
            "improving variable assignments and expressions, analyzing data flow for other optimizations"),

    INSTRUCTION_REORDERING("Instruction Reordering",
            InstructionReordering::new,
            "reordering instructions to allow additional optimizations being made (not available yet)"),

    LOOP_HOISTING("Loop Hoisting",
            LoopHoisting::new,
            "moving invariant code out of loops"),

    LOOP_ROTATION("Loop Rotation",
            LoopRotator::new,
            "rotating a front loop condition to the bottom of the loop"),

    LOOP_UNROLLING("Loop Unrolling",
            LoopUnroller::new,
            "unrolling loops with a fixed number of iterations"),

    FUNCTION_INLINING("Function Inlining",
            FunctionInliner::new,
            "inlining stackless function calls"),

    ARRAY_OPTIMIZATION("Array Optimization",
            ArrayOptimizer::new,
            "finding optimal array-access implementation for internal arrays"),

    CASE_SWITCHING("Case Switching",
            CaseSwitcher::new,
            "modifying suitable case expressions to use jump tables or value translations"),

    RETURN_OPTIMIZATION("Return Optimization",
            ReturnOptimizer::new,
            "speeding up return statements in recursive and stackless functions"),

    JUMP_STRAIGHTENING("Jump Straightening",
            JumpStraightening::new,
            "simplifying sequences of intertwined jumps"),

    JUMP_THREADING("Jump Threading",
            JumpThreading::new,
            "eliminating chained jumps"),

    UNREACHABLE_CODE_ELIMINATION("Unreachable Code Elimination",
            UnreachableCodeEliminator::new,
            "eliminating instructions made unreachable by optimizations or false conditions"),

    STACK_OPTIMIZATION("Stack Optimization",
            StackOptimizer::new,
            "optimizing variable storage on the stack"),

    PRINT_MERGING("Print Merging",
            PrintMerger::new,
            "merging consecutive print statements outputting constant values"),
    ;

    private final String name;
    private final Function<OptimizationContext, Optimizer> instanceCreator;
    private final String optionName;
    private final String description;
    private final Predicate<OptimizationContext> availability;

    Optimization(String name, Function<OptimizationContext, Optimizer> instanceCreator, String description) {
        this.name = name;
        this.optionName = name.toLowerCase().replace(' ', '-');
        this.instanceCreator = instanceCreator;
        this.description = description;
        this.availability = _ -> true;
    }

    Optimization(String name, Predicate<OptimizationContext> availability,
            Function<OptimizationContext, Optimizer> instanceCreator, String description) {
        this.name = name;
        this.optionName = name.toLowerCase().replace(' ', '-');
        this.instanceCreator = instanceCreator;
        this.description = description;
        this.availability = availability;
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

    Function<OptimizationContext, Optimizer> getInstanceCreator() {
        return instanceCreator;
    }

    public boolean isAvailable(OptimizationContext optimizationContext) {
        return availability.test(optimizationContext);
    }

    public static final List<Optimization> LIST = List.of(values());
}
