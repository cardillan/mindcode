package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.logic.TypedArgument;

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
class FunctionParameterOptimizer extends BaseFunctionOptimizer {
    public FunctionParameterOptimizer(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    @Override
    protected boolean optimizeProgram() {
        // Find all functions (includes inline ones)
        List<String> prefixes = getFunctions();

        prefixes.forEach(this::optimizeFunction);

        return true;
    }

    private void optimizeFunction(String functionPrefix) {
        List<LogicInstruction> function = getFunctionInstructions(functionPrefix);

        // Count the number of times each variable is modified in the function
        Map<String, Long> modifications = function.stream()
                .flatMap(instructionProcessor::getTypedArguments)
                .filter(a -> a.getArgumentType().isOutput())
                .map(TypedArgument::getValue)
                .collect(Collectors.groupingBy(a -> a, Collectors.counting()));

        boolean hasCalls = function.stream().anyMatch(this::isCall);

        // Obtain a list of set instructions that can be eliminated.
        // See preconditions listed in class javadoc
        List<SetInstruction> eliminations = function.stream()
                .filter(ix -> ix instanceof SetInstruction)                                         // precondition 1
                .map(ix -> (SetInstruction) ix)
                .filter(ix -> ix.getResult().startsWith(functionPrefix + "_"))                      // precondition 1
                .filter(ix -> modifications.get(ix.getResult()) == 1)                               // precondition 2
                .filter(ix -> !modifications.containsKey(ix.getValue()))                            // precondition 3
                .filter(ix -> !instructionProcessor.isVolatile(ix.getValue()))                      // precondition 3
                .filter(ix -> !hasCalls || !instructionProcessor.isGlobalName(ix.getValue()))       // precondition 4
                .toList();

        eliminations.forEach(ix -> eliminateInstruction(functionPrefix, ix));
    }

    private boolean isCall(LogicInstruction instr) {
        // Stackless functions are called via set @counter __label
        // TODO: replace with special CALL instruction
        return instr instanceof CallInstruction || (instr instanceof SetInstruction ix && ix.getResult().equals("@counter"));
    }
}
