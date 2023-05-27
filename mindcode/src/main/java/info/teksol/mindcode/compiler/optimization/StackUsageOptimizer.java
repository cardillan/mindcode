package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.AstContext;
import info.teksol.mindcode.compiler.instructions.AstContextType;
import info.teksol.mindcode.compiler.instructions.CallRecInstruction;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.PushOrPopInstruction;
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
 * Removes variables from stack matching the following conditions:
 * <ol><li>The variable isn't read by any instruction following the call instruction, up to the end of the function.
 * </li><li>The variable isn't read by any instruction in any loop shared with the call instruction.
 * </li></ol>
 * </li></ul>
 */
public class StackUsageOptimizer extends AstContextOptimizer {

    StackUsageOptimizer(InstructionProcessor instructionProcessor) {
        super(instructionProcessor);
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
        List<LogicArgument> list = instructionStream()
                .filter(Predicate.not(PushOrPopInstruction.class::isInstance))
                .flatMap(LogicInstruction::inputOutputArgumentsStream)
                .filter(LogicVariable.class::isInstance)
                .toList();

        variables.addAll(list);

        removeMatchingInstructions(this::uselessStackOperation);
    }

    private boolean uselessStackOperation(LogicInstruction instruction) {
        return instruction instanceof PushOrPopInstruction ix && !variables.contains(ix.getVariable());
    }

    private void removeUnnecessaryPushes() {
        try (LogicIterator it = createIterator()) {
            while (it.hasNext()) {
                if (it.next() instanceof CallRecInstruction call) {
                    // Obtain entire function
                    AstContext functionContext = call.findContextOfType(AstContextType.FUNCTION);
                    if (functionContext == null) {
                        continue;   // If there's no function context, we're in main body. Skip call.
                    }

                    LogicList function = contextInstructions(functionContext);

                    // 1. Preserve all variables that are read anywhere after the call
                    int callIndex = function.indexOf(ix -> ix == call);
                    LogicList codeBlock = function.subList(callIndex, function.size());
                    Set<LogicArgument> preserveVariables = codeBlock.stream()
                            .filter(ix -> !(ix instanceof PushOrPopInstruction))
                            .flatMap(LogicInstruction::inputArgumentsStream)
                            .filter(LogicVariable.class::isInstance)
                            .collect(Collectors.toCollection(HashSet::new));

                    // 2. Preserve all variables which are part of the same loop as the call. As the loops are
                    // hierarchical, we can inspect just the topmost loop.
                    contextStream(call.findTopContextOfType(AstContextType.LOOP))
                            .filter(ix -> !(ix instanceof PushOrPopInstruction))
                            .flatMap(LogicInstruction::inputArgumentsStream)
                            .forEachOrdered(preserveVariables::add);

                    // Remove instructions from the function
                    function.stream()
                            .filter(in -> in instanceof PushOrPopInstruction ix &&
                                    ix.getAstContext().matches(call.getAstContext()) &&
                                    !preserveVariables.contains(ix.getVariable()))
                            .forEachOrdered(this::removeInstruction);
                }
            }
        }
    }
}
