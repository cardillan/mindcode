package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.*;

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
        Set<String> localLabels = codeBlock.stream()
                .filter(ix -> ix instanceof LabelInstruction)
                .map(ix -> ((LabelInstruction) ix).getLabel())
                .collect(Collectors.toSet());

        // Code is linear if every jump targets a local label
        return codeBlock.stream()
                .filter(ix -> ix instanceof JumpInstruction || ix instanceof GotoInstruction || ix instanceof EndInstruction)
                .flatMap(this::getPossibleTargetLabels)
                .allMatch(localLabels::contains);
    }

    private Stream<String> getPossibleTargetLabels(LogicInstruction instruction) {
        return switch (instruction) {
            case EndInstruction  ix -> Stream.of("0");               // Impossible label: end() is never local
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
        String oldArg = ix.getResult();
        String newArg = ix.getValue();
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
