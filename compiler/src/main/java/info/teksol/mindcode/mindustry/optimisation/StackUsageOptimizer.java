package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.logic.TypedArgument;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Optimizes the stack usage -- eliminates push/pop instruction pairs determined to be unnecessary. Several
 * independent optimizations are performed:
 *
 * <ul><li>
 * Eliminates push/pop instruction for variables that are not used anywhere else (after being eliminated
 * by other optimizers). The optimization is done globally, in a single pass across the entire program.
 * </li><li>
 * Inspects every remaining variable pushed/popped around a function call. If the block of code between the function
 * call and the end of the function is linear (doesn't contain jumps outside of the code block -- function calls aren't
 * considered) and the variable is not read in the code block, it is removed from the stack.
 * </li></ul>
 */
public class StackUsageOptimizer extends GlobalOptimizer {

    StackUsageOptimizer(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    @Override
    protected boolean optimizeProgram() {
        // Both optimizations handle the program body and user functions at once. The program body can contain call
        // instructions, but no push or pop instructions, so the optimizations won't do anything on them,
        // even if processing the program body might be a bit ineffective.
        removeUnusedVariables();
        removeUnnecessaryPushes();
        return false;
    }

    private final Set<String> variables = new HashSet<>();

    private void removeUnusedVariables() {
        program.stream().filter(ix -> !ix.isPushOrPop()).forEach(this::collectVariables);
        program.removeIf(this::uselessStackOperation);
    }

    private void collectVariables(LogicInstruction instruction) {
        variables.addAll(instructionProcessor.getInputOutputValues(instruction));
    }

    private boolean uselessStackOperation(LogicInstruction instruction) {
        return instruction.isPushOrPop() && !variables.contains(instruction.getArg(1));
    }

    private void removeUnnecessaryPushes() {
        // Loop through call instruction indexes
        int call = findInstructionIndex(0, LogicInstruction::isCall);
        while (call > 0) {
            int finish = findInstructionIndex(call, LogicInstruction::isReturn);
            if (finish < 0) {
                break;      // No return after call: something is wrong, bail out.
            }

            List<LogicInstruction> codeBlock = program.subList(call, finish);
            if (isLinear(codeBlock)) {
                // List of variables read in the code block, except push/pop operations
                Set<String> readVariables = codeBlock.stream()
                        .filter(ix -> !ix.isPushOrPop())
                        .flatMap(ix -> instructionProcessor.getTypedArguments(ix))
                        .filter(arg -> arg.getArgumentType().isInput())
                        .map(TypedArgument::getValue)
                        .collect(Collectors.toSet());

                // Push/pop instructions around a call are marked with the same marker
                String marker = program.get(call).getMarker();

                // Need to remove from the entire program, not just from the code block, as code block
                // doesn't contain push instructions preceding the call instruction
                program.removeIf(ix -> ix.isPushOrPop() && ix.matchesMarker(marker)
                        && !readVariables.contains(ix.getArg(1)));
            }

            call = findInstructionIndex(call + 1, LogicInstruction::isCall);
        }
    }

    private boolean isLinear(List<LogicInstruction> codeBlock) {
        Set<String> localLabels = codeBlock.stream().filter(LogicInstruction::isLabel).map(ix -> ix.getArg(0)).collect(Collectors.toSet());

        // Code is linear if every jump targets a local label
        return codeBlock.stream()
                .filter(ix -> ix.isJump() || ix.isGoto())
                .flatMap(this::getPossibleTargetLabels).allMatch(localLabels::contains);
    }

    private Stream<String> getPossibleTargetLabels(LogicInstruction instruction) {
        switch (instruction.getOpcode()) {
            case JUMP:
                return Stream.of(instruction.getArg(0));

            case GOTO:
                return program.stream()
                        .filter(ix -> ix.isLabel() && ix.matchesMarker(instruction.getMarker()))
                        .map(ix -> ix.getArg(0));

            default:
                return Stream.empty();
        }
    }
}
