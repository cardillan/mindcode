package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicVariable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Optimizes the stack usage -- eliminates push/pop instruction pairs determined to be unnecessary. Several
 * independent optimizations are performed:
 * <ul><li>
 * Eliminates push/pop instruction for variables that are not used anywhere else (after being eliminated
 * by other optimizers). The optimization is done globally, in a single pass across the entire program.
 * </li><li>
 * Inspects every remaining variable pushed/popped around a function call. If the block of code between the function
 * call and the end of the function is linear (doesn't contain jumps into the code block from outside -- function
 * calls aren't considered) and the variable is not read in the code block, it is removed from the stack.
 * </li></ul>
 */
public class StackUsageOptimizer extends BaseFunctionOptimizer {

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

    private final Set<LogicArgument> variables = new HashSet<>();

    private void removeUnusedVariables() {
        // Collects all variables from the entire program except push or pop instructions
        program.stream().filter(Predicate.not(PushOrPopInstruction.class::isInstance))
                .flatMap(LogicInstruction::inputOutputArgumentsStream)
                .filter(LogicVariable.class::isInstance)
                .forEachOrdered(variables::add);

        program.removeIf(this::uselessStackOperation);
    }

    private boolean uselessStackOperation(LogicInstruction instruction) {
        return instruction instanceof PushOrPopInstruction ix && !variables.contains(ix.getVariable());
    }

    private void removeUnnecessaryPushes() {
        // Loop through call instruction indexes
        int call = findInstructionIndex(0, ix -> ix instanceof CallRecInstruction);
        while (call > 0) {
            int finish = findInstructionIndex(call, ix -> ix instanceof ReturnInstruction);
            if (finish < 0) {
                break;      // No return after call: something is wrong, bail out.
            }

            List<LogicInstruction> codeBlock = program.subList(call, finish);
            if (isLocalized(codeBlock)) {
                // List of variables read in the code block, except push/pop operations
                Set<LogicArgument> readVariables = codeBlock.stream()
                        .filter(ix -> !(ix instanceof PushOrPopInstruction))
                        .flatMap(LogicInstruction::inputArgumentsStream)
                        .collect(Collectors.toSet());

                // Push/pop instructions around a call are marked with the same marker
                String marker = program.get(call).getMarker();

                // Need to remove from the entire program, not just from the code block, as code block
                // doesn't contain push instructions preceding the call instruction
                program.removeIf(in -> in instanceof PushOrPopInstruction ix
                        && ix.matchesMarker(marker) && !readVariables.contains(ix.getVariable()));
            }

            call = findInstructionIndex(call + 1, ix -> ix instanceof CallRecInstruction);
        }
    }

}
