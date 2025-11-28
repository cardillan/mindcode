package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.logic.opcodes.Opcode;
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

    JUMP_OPTIMIZATION("Jump Optimization",
            JumpOptimizer::new,
            "merging an op instruction producing a boolean expression into the following conditional jump"),

    SINGLE_STEP_ELIMINATION("Single Step Elimination",
            SingleStepEliminator::new,
            "eliminating jumps to the next instruction"),

    EXPRESSION_OPTIMIZATION("Expression Optimization",
            ExpressionOptimizer::new,
            "optimizing some common mathematical expressions"),

    SELECT_OPTIMIZATION("Select Optimization",
            context -> context.getInstructionProcessor().isSupported(Opcode.SELECT),
            SelectOptimizer::new,
            "expressing conditional expressions using the 'select' instruction"),

    IF_EXPRESSION_OPTIMIZATION("If Expression Optimization",
            IfExpressionOptimizer::new,
            "improving ternary/if expressions"),

    DATA_FLOW_OPTIMIZATION("Data Flow Optimization",
            DataFlowOptimizer::new,
            "improving variable assignments and and expressions"),

    LOOP_HOISTING("Loop Hoisting",
            LoopHoisting::new,
            "moving invariant code out of loops"),

    LOOP_OPTIMIZATION("Loop Optimization",
            LoopOptimizer::new,
            "improving loops"),

    LOOP_UNROLLING("Loop Unrolling",
            LoopUnroller::new,
            "unrolling loops with constant number of iterations (optimization for speed)"),

    FUNCTION_INLINING("Function Inlining",
            FunctionInliner::new,
            "inlining stackless function calls (optimization for speed)"),

    ARRAY_OPTIMIZATION("Array Optimization",
            ArrayOptimizer::new,
            "improving internal array random access mechanism (optimization for speed)"),

    CASE_SWITCHING("Case Switching",
            CaseSwitcher::new,
            "modifying suitable case expressions to use jump tables"),

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
            "optimizing variable storage on stack"),

    PRINT_MERGING("Print Merging",
            PrintMerger::new,
            "merging consecutive print statements outputting text literals"),
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
