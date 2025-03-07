package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.PushOrPopInstruction;
import info.teksol.mc.mindcode.logic.instructions.SetInstruction;
import info.teksol.mc.mindcode.logic.opcodes.TypedArgument;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/// Generic optimizer to remove all assignments to temporary variables that carry over the output value
/// of the preceding instruction. The `set` instruction is removed, while the preceding instruction is updated
/// to replace the temp variable with the target variable used in the `set` instruction.
/// The optimization is performed only when the following conditions are met:
///
/// - The set instruction assigns from a temporary variable.
/// - The temporary variable is used in exactly one other instruction, which immediately precedes the instruction
///   producing the temporary variable
/// - All arguments of the other instruction referencing the temporary variable are output ones.
///
/// This optimizer ignores push and pop instructions. The StackUsageOptimizer will remove push/pop instructions of any
/// eliminated variables later on.
@NullMarked
class TempVariableEliminator extends BaseOptimizer {
    private @Nullable Set<LogicArgument> inputTempVariables;

    public TempVariableEliminator(OptimizationContext optimizationContext) {
        super(Optimization.TEMP_VARIABLES_ELIMINATION, optimizationContext);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        inputTempVariables = gatherInputTempVariables();

        try (LogicIterator itCurr = createIterator(); LogicIterator itPrev = createIterator()) {
            if (itCurr.hasNext()) {
                // Skip first, but process unused outputs
                replaceUnusedOutputs(itCurr);
            }

            while (itCurr.hasNext()) {
                LogicInstruction current = replaceUnusedOutputs(itCurr);
                LogicInstruction previous = itPrev.next();

                if (current instanceof SetInstruction set && set.getValue().isTemporaryVariable()) {
                    LogicArgument value = set.getValue();
                    List<LogicInstruction> list = instructions(
                            ix -> ix.getArgs().contains(value) && !(ix instanceof PushOrPopInstruction));

                    // Not exactly two instructions, or the previous instruction doesn't produce the tmp variable
                    if (list.size() == 2 && list.getFirst() == previous) {
                        // Make sure all arg1 arguments of the other instruction are output
                        boolean replacesOutputArg = previous.typedArgumentsStream()
                                .filter(t -> t.argument().equals(value))
                                .allMatch(TypedArgument::isOutput);

                        if (replacesOutputArg) {
                            // The current instruction merely transfers a value from the output argument of the previous instruction
                            // Replacing those arguments with target of the set instruction
                            itPrev.set(replaceAllArgs(previous, value, set.getResult()).withContext(set.getAstContext()));
                            itCurr.set(createNoOp(current.getAstContext()));
                        }
                    }
                }
            }
        }

        return false;
    }

    private LogicInstruction replaceUnusedOutputs(LogicIterator iterator) {
        assert inputTempVariables != null;
        LogicInstruction instruction = iterator.next();
        if (instruction.getOutputs() > 0) {
            List<LogicArgument> unusedTemps = instruction.outputArgumentsStream()
                    .filter(LogicArgument::isTemporaryVariable)
                    .filter(var -> !inputTempVariables.contains(var))
                    .toList();

            for (LogicArgument unusedTemp : unusedTemps) {
                instruction = replaceAllArgs(instruction, unusedTemp, instructionProcessor.unusedVariable());
                iterator.set(instruction);
            }
        }

        return instruction;
    }

    private Set<LogicArgument> gatherInputTempVariables() {
        return optimizationContext.instructionStream()
                .flatMap(LogicInstruction::inputArgumentsStream)
                .filter(LogicArgument::isTemporaryVariable)
                .collect(Collectors.toSet());
    }
}
