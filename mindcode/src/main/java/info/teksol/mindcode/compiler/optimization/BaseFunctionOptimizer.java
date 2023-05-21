package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LabelInstruction;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.SetInstruction;
import info.teksol.mindcode.logic.LogicArgument;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class BaseFunctionOptimizer extends GlobalOptimizer {
    public BaseFunctionOptimizer(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    /**
     * Creates an independent list (not a sublist of the main program) of instructions belonging to given function.
     *
     * @param functionPrefix prefix assigned to the function realization
     * @return list of instructions belonging to the function
     */
    protected List<LogicInstruction> getFunctionInstructions(String functionPrefix) {
        // Find start and end labels
        int[] bounds = instructionStream()
                .filter(ix -> ix instanceof LabelInstruction)
                .filter(ix -> ix.matchesMarker(functionPrefix))
                .mapToInt(ix -> findInstructionIndex(0, z -> z == ix))
                .toArray();

        if (bounds.length != 2) {
            return List.of();
        } else {
            return List.copyOf(instructionSubList(bounds[0], bounds[1]));
        }
    }

    /**
     * Creates a list of all functions present in the compiled code. Functions are identified by their prefixes.
     *
     * @return list of existing function prefixes.
     */
    protected List<String> getFunctions() {
        return instructionStream()
                .filter(ix -> ix instanceof LabelInstruction)
                .map(LogicInstruction::getMarker)
                .filter(Objects::nonNull)
                .filter(s -> s.startsWith(instructionProcessor.getLocalPrefix()))
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Eliminates given set instruction and replaces all occurrences of target variable with the assigned value.
     *
     * @param functionPrefix function to be modified
     * @param ix instruction to eliminate
     */
    protected void eliminateInstruction(String functionPrefix, SetInstruction ix) {
        LogicArgument oldArg = ix.getTarget();
        LogicArgument newArg = ix.getValue();
        removeInstruction(ix);

        // The function needs to be collected again, as it could have been modified by previous replacement
        List<LogicInstruction> function = getFunctionInstructions(functionPrefix);

        for (LogicInstruction current : function) {
            if (current.getArgs().contains(oldArg)) {
                replaceInstruction(current, replaceAllArgs(current, oldArg, newArg));
            }
        }
    }
}
