package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import info.teksol.mindcode.mindustry.generator.GenerationException;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.logic.TypedArgument;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Removes unnecessary function parameters and local variables (replaces them by the argument/value assigned to them).
 * Significantly improves inline functions, but seldom might help with other functions as well. The optimizer
 * processes individual functions (inline and out-of-line) one by one and searches for SET instructions assigning
 * a value to a variable (i.e. "set target value") and checks these preconditions are met:
 * <p>
 * 1. The target variable is a local variable or parameter -- has the function prefix followed by an underscore,
 *    e.g. "__fn0_". This excludes __fnXretaddr and __fnXretval variables, which must be preserved.
 * <p>
 * 2. The target variable is modified exactly once, i.e. there isn't any other instruction besides the original SET
 *    instruction which would modify the variable. This includes push/pop instructions, but if those are unnecessary,
 *    they would be already removed by prior optimizers.
 * <p>
 * 3. The value being assigned to the target variable is not volatile (e.g. @time, @tick, @counter etc.) and is not
 *    modified anywhere in the function.
 * <p>
 * 4. If the function contains a CALL instruction, the value is not a global variable. Global variables might be
 *    modified by the called function. (Block names, while technically also global, are not affected, as these are
 *    effectively constant.)
 * <p>
 * When the conditions are met, the following happens:
 *  i) The original instruction assigning value to the target variable is removed.
 * ii) Every remaining occurrence of target variable is replaced with the assigned value.
 * <p>
 * Functions are located in the code using the entry and exit labels marked with function prefix.
 */
class FunctionParameterOptimizer extends GlobalOptimizer {
    public FunctionParameterOptimizer(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    @Override
    protected boolean optimizeProgram() {
        // Find all functions (includes inline ones)
        List<String> prefixes = program.stream()
                .filter(LogicInstruction::isLabel)
                .map(LogicInstruction::getMarker)
                .filter(Objects::nonNull)
                .filter(s -> s.startsWith(instructionProcessor.getLocalPrefix()))
                .distinct()
                .collect(Collectors.toList());

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
        List<LogicInstruction> eliminations = function.stream()
                .filter(LogicInstruction::isSet)                                                // precondition 1
                .filter(ix -> ix.getArg(0).startsWith(functionPrefix + "_"))                    // precondition 1
                .filter(ix -> modifications.get(ix.getArg(0)) == 1)                             // precondition 2
                .filter(ix -> !modifications.containsKey(ix.getArg(1)))                         // precondition 3
                .filter(ix -> !instructionProcessor.isVolatile(ix.getArg(1)))                   // precondition 3
                .filter(ix -> !hasCalls || !instructionProcessor.isGlobalName(ix.getArg(1)))    // precondition 4
                .collect(Collectors.toList());

        eliminations.forEach(ix -> eliminateInstruction(functionPrefix, ix));
    }

    private boolean isCall(LogicInstruction ix) {
        return ix.isCall() || (ix.isSet() && ix.getArg(0).equals("@counter"));
    }

    /**
     * Eliminates given instruction and replaces all occurrences of target variable with the assigned value.
     *
     * @param functionPrefix function to be modified
     * @param ix instruction to eliminate
     */
    private void eliminateInstruction(String functionPrefix, LogicInstruction ix) {
        if (!ix.isSet()) {
            throw new GenerationException("SET instruction expected, got " + ix);
        }
        String oldArg = ix.getArg(0);
        String newArg = ix.getArg(1);
        removeInstruction(ix);

        // The function needs to be collected again, as it could have been modified by previous replacement
        List<LogicInstruction> function = getFunctionInstructions(functionPrefix);

        for (LogicInstruction current : function) {
            if (current.getArgs().contains(oldArg)) {
                replaceInstruction(current, replaceAllArgs(current, oldArg, newArg));
            }
        }
    }

    /**
     * Creates an independent list (not a sublist of the main program) of instructions belonging to given function.
     * 
     * @param functionPrefix prefix assigned to the function realization
     * @return list of instructions belonging to the function
     */
    private List<LogicInstruction> getFunctionInstructions(String functionPrefix) {
        // Find start and end labels
        int[] bounds = program.stream()
                .filter(LogicInstruction::isLabel)
                .filter(ix -> ix.matchesMarker(functionPrefix))
                .mapToInt(ix -> findInstructionIndex(0, z -> z == ix))
                .toArray();

        if (bounds.length != 2) {
            return List.of();
        } else {
            return List.copyOf(program.subList(bounds[0], bounds[1]));
        }
    }
}
