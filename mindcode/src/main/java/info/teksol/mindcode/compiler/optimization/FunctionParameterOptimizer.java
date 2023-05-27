package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.AstContext;
import info.teksol.mindcode.compiler.instructions.CallInstruction;
import info.teksol.mindcode.compiler.instructions.CallRecInstruction;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.SetInstruction;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.ParameterAssignment;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Removes unnecessary function parameters and local variables (replaces them by the argument/value assigned to them).
 * Significantly improves inline functions, but seldom might help with other functions as well. The optimizer
 * processes individual functions (inline and out-of-line) one by one and searches for {@code set} instructions
 * assigning a value to a variable (i.e. {@code set target value}) and checks these preconditions are met:
 * <ol>
 * <li>The target variable is a local variable or parameter -- has the function prefix followed by an underscore,
 * e.g. {@code __fn0_}. This excludes {@code __fnXretaddr} and {@code __fnXretval} variables, which must be preserved.</li>
 * <li>The target variable is modified exactly once, i.e. there isn't any other instruction besides the original
 * {@code set} instruction which would modify the variable. This includes push/pop instructions, but if those are
 * unnecessary, they would be already removed by prior optimizers.</li>
 * <li>The value being assigned to the target variable is not volatile (e.g. {@code @time}, {@code @tick},
 * {@code @counter} etc.) and is not modified anywhere in the function.</li>
 * <li>If the function contains a {@code call} instruction, the value is not a global variable. Global variables
 * might be modified by the called function. (Block names, while technically also global, are not affected, as these
 * are effectively constant.)</li>
 * </ol>
 * When these preconditions are met, the following happens:
 * <ul>
 * <li>The original instruction assigning value to the target variable is removed.</li>
 * <li>Every remaining occurrence of target variable is replaced with the assigned value.</li>
 * </ul>
 * Functions are located in the code using the entry and exit labels marked with function prefix.
 */
class FunctionParameterOptimizer extends AstContextOptimizer {
    public FunctionParameterOptimizer(InstructionProcessor instructionProcessor) {
        super(instructionProcessor);
    }

    @Override
    protected boolean optimizeProgram() {
        // Find all functions (includes inline ones)
        getInlineFunctions().forEach(this::optimizeFunction);
        getOutOfLineFunctions().forEach(this::optimizeFunction);
        return true;
    }

    private void optimizeFunction(AstContext astContext) {
        LogicList function = contextInstructions(astContext);

        // Count the number of times each variable is modified in the function
        Map<LogicArgument, Long> modifications = function.stream()
                .flatMap(LogicInstruction::assignmentsStream)
                .filter(ParameterAssignment::isOutput)
                .map(ParameterAssignment::argument)
                .collect(Collectors.groupingBy(a -> a, Collectors.counting()));

        boolean hasCalls = function.stream().anyMatch(this::isCall);

        // Obtain a list of set instructions that can be eliminated.
        // See preconditions listed in class javadoc
        List<SetInstruction> eliminations = function.stream()
                .filter(ix -> ix instanceof SetInstruction)                         // precondition 1
                .map(ix -> (SetInstruction) ix)
                .filter(ix -> ix.getResult().isFunctionVariable())                  // precondition 1
                .filter(ix -> modifications.get(ix.getResult()) == 1)               // precondition 2
                .filter(ix -> !modifications.containsKey(ix.getValue()))            // precondition 3
                .filter(ix -> !ix.getValue().isVolatile())                          // precondition 3
                .filter(ix -> !hasCalls || !ix.getValue().isGlobalVariable())       // precondition 4
                .toList();

        eliminations.forEach(ix -> eliminateInstruction(astContext, ix));
    }

    private boolean isCall(LogicInstruction ix) {
        return ix instanceof CallRecInstruction || ix instanceof CallInstruction;
    }

    /**
     * Eliminates given set instruction and replaces all occurrences of target variable with the assigned value.
     *
     * @param astContext context representing function to be modified
     * @param ix instruction to eliminate
     */
    protected void eliminateInstruction(AstContext astContext, SetInstruction ix) {
        LogicArgument oldArg = ix.getResult();
        LogicArgument newArg = ix.getValue();
        removeInstruction(ix);

        // The function needs to be collected again, as it could have been modified by previous replacement
        LogicList function = contextInstructions(astContext);

        for (LogicInstruction current : function) {
            if (current.getArgs().contains(oldArg)) {
                replaceInstruction(current, replaceAllArgs(current, oldArg, newArg));
            }
        }
    }
}
