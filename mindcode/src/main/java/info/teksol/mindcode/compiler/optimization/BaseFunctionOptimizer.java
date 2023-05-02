package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicLabel;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseFunctionOptimizer extends GlobalOptimizer {
    public BaseFunctionOptimizer(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    protected boolean isLinear(List<LogicInstruction> codeBlock) {
        Set<LogicLabel> localLabels = codeBlock.stream()
                .filter(ix -> ix instanceof LabelInstruction)
                .map(ix -> ((LabelInstruction) ix).getLabel())
                .collect(Collectors.toSet());

        // Get jump/goto instructions targeting any of local labels
        // If all of them are local to the code block, the code block is linear
        return program.stream()
                .filter(ix -> ix instanceof JumpInstruction || ix instanceof GotoInstruction)
                .filter(ix -> getPossibleTargetLabels(ix).anyMatch(localLabels::contains))
                .allMatch(ix -> codeBlock.stream().anyMatch(local -> local == ix));
    }

    private Stream<LogicLabel> getPossibleTargetLabels(LogicInstruction instruction) {
        return switch (instruction) {
            case JumpInstruction ix -> Stream.of(ix.getTarget());
            case GotoInstruction ix -> program.stream()
                    .filter(in -> in instanceof LabelInstruction && in.matchesMarker(ix.getMarker()))
                    .map(in -> (LabelInstruction) in)
                    .map(LabelInstruction::getLabel);
            default -> Stream.empty();
        };
    }

    /**
     * Creates an independent list (not a sublist of the main program) of instructions belonging to given function.
     *
     * @param functionPrefix prefix assigned to the function realization
     * @return list of instructions belonging to the function
     */
    protected List<LogicInstruction> getFunctionInstructions(String functionPrefix) {
        // Find start and end labels
        int[] bounds = program.stream()
                .filter(ix -> ix instanceof LabelInstruction)
                .filter(ix -> ix.matchesMarker(functionPrefix))
                .mapToInt(ix -> findInstructionIndex(0, z -> z == ix))
                .toArray();

        if (bounds.length != 2) {
            return List.of();
        } else {
            return List.copyOf(program.subList(bounds[0], bounds[1]));
        }
    }

    /**
     * Creates a list of all functions present in the compiled code. Functions are identified by their prefixes.
     *
     * @return list of existing function prefixes.
     */
    protected List<String> getFunctions() {
        return program.stream()
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
