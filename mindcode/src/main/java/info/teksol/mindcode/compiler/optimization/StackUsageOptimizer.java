package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.generator.CallGraph.Function;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicVariable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
 * <b>Note:</b> a variable may be read implicitly by a recursive call.
 * </li><li>
 * Eliminates push/pop instruction for variables that are not modified at all by the function.
 * </li></ul>
 */
public class StackUsageOptimizer extends BaseOptimizer {

    StackUsageOptimizer(OptimizationContext optimizationContext) {
        super(Optimization.STACK_USAGE_OPTIMIZATION, optimizationContext);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase, int pass, int iteration) {
        if (getCallGraph().containsRecursiveFunction()) {
            // Both optimizations handle the program body and user functions at once. The program body can contain call
            // instructions, but no push or pop instructions, so the optimizations won't do anything on them,
            // even if processing the program body might be a bit ineffective.
            removeUnusedVariables();
            removeUnnecessaryPushes();
        }
        return false;
    }

    private final Set<LogicArgument> variables = new HashSet<>();

    private void removeUnusedVariables() {
        // Collects all variables from the entire program except push or pop instructions
        Set<LogicArgument> variables = instructionStream()
                .filter(Predicate.not(PushOrPopInstruction.class::isInstance))
                .flatMap(LogicInstruction::inputOutputArgumentsStream)
                .filter(LogicVariable.class::isInstance)
                .collect(Collectors.toSet());

        removeMatchingInstructions(ix -> uselessStackOperation(variables, ix));
    }

    private boolean uselessStackOperation(Set<LogicArgument> variables, LogicInstruction instruction) {
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
                    LogicList codeBlock = function.subList(callIndex + 1, function.size());
                    Set<LogicVariable> preserveVariables = codeBlock.stream()
                            .filter(ix -> !(ix instanceof PushOrPopInstruction))
                            .flatMap(this::readVariables)
                            .collect(Collectors.toCollection(HashSet::new));

                    // 2. Preserve all variables which are part of the same loop as the call. As the loops are
                    // hierarchical, we can inspect just the topmost loop.
                    contextStream(call.findTopContextOfType(AstContextType.LOOP))
                            .filter(ix -> !(ix instanceof PushOrPopInstruction))
                            .flatMap(LogicInstruction::inputArgumentsStream)
                            .filter(LogicVariable.class::isInstance)
                            .map(LogicVariable.class::cast)
                            .forEachOrdered(preserveVariables::add);

                    // 3. Remove all variables that are not written to in the entire function
                    Set<LogicVariable> writtenVariables = function.stream()
                            .filter(ix -> !(ix instanceof PushOrPopInstruction))
                            .flatMap(LogicInstruction::outputArgumentsStream)
                            .filter(LogicVariable.class::isInstance)
                            .map(LogicVariable.class::cast)
                            .collect(Collectors.toCollection(HashSet::new));

                    // Only keep variables written to
                    preserveVariables.retainAll(writtenVariables);

                    // Remove instructions from the function
                    function.stream()
                            .filter(in -> in instanceof PushOrPopInstruction ix &&
                                    ix.getAstContext().belongsTo(call.getAstContext()) &&
                                    !preserveVariables.contains(ix.getVariable()))
                            .forEachOrdered(this::removeInstruction);
                }
            }
        }
    }

    private Stream<LogicVariable> readVariables(LogicInstruction instruction) {
        if (instruction instanceof CallRecInstruction callRecInstruction) {
            return implicitVariableReads(callRecInstruction).stream();
        } else {
            return instruction.inputArgumentsStream()
                    .filter(LogicVariable.class::isInstance)
                    .map(LogicVariable.class::cast);
        }
    }

    private Set<LogicVariable> implicitVariableReads(CallRecInstruction instruction) {
        AstContext subcontext = instruction.getAstContext();
        if (subcontext.subcontextType() != AstSubcontextType.RECURSIVE_CALL) {
            throw new MindcodeInternalError("Expected RECURSIVE_CALL subcontext, found " + subcontext);
        }

        Function function = getCallGraph().getFunctionByPrefix(subcontext.functionPrefix());
        Set<LogicVariable> result = new HashSet<>(function.getLogicParameters());

        contextStream(subcontext)
                .filter(SetInstruction.class::isInstance)
                .map(SetInstruction.class::cast)
                .map(SetInstruction::getResult)
                .forEach(result::remove);

        return result;
    }
}
