package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.instructions.EmptyInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.PushOrPopInstruction;
import info.teksol.mc.mindcode.logic.instructions.SetInstruction;
import info.teksol.mc.mindcode.logic.opcodes.TypedArgument;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/// Generic optimizer to remove all assignments to temporary variables that only carry the value to/from
/// the adjacent instruction. The `set` instruction is removed, while the adjacent instruction is updated
/// to replace the temp variable with the other variable in the `set` instruction.
///
/// The optimization is performed only when the following conditions are met:
///
/// - The `set` instruction assigns/reads a temporary variable.
/// - The temporary variable is used in exactly one other instruction, adjacent to the `set` instruction.
/// - All arguments of the other instruction referencing the temporary variable are either input ones (the `set`
///   instruction precedes the other instruction) or output ones (the `set` instruction follows the other instruction).
///
/// This optimizer ignores push and pop instructions. The StackUsageOptimizer will remove push/pop instructions of any
/// eliminated variables later on.
@NullMarked
class TempVariableEliminator extends BaseOptimizer {

    public TempVariableEliminator(OptimizationContext optimizationContext) {
        super(Optimization.TEMP_VARIABLES_ELIMINATION, optimizationContext);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        Set<LogicArgument> inputTempVariables = gatherInputTempVariables();
        if (phase == OptimizationPhase.FINAL) {
            try (LogicIterator itCurr = createIterator()) {
                while (itCurr.hasNext()) {
                    replaceUnusedOutputs(inputTempVariables, itCurr);
                }
            }
        }

        List<LogicInstruction> program = optimizationContext.getProgram();
        Map<LogicArgument, SortedSet<Integer>> variableUses = gatherTempVariableUses(program);

        boolean replaced = false;
        for (Map.Entry<LogicArgument, SortedSet<Integer>> entry : variableUses.entrySet()) {
            LogicArgument variable = entry.getKey();
            SortedSet<Integer> indices = entry.getValue();
            if (indices.size() == 2 && program.subList(indices.getFirst() + 1, indices.getLast()).stream().allMatch(EmptyInstruction.class::isInstance)) {
                // This temp variable is used by two consecutive instructions, designated 'first' and 'last'
                LogicInstruction first = instructionAt(indices.getFirst());
                LogicInstruction last = instructionAt(indices.getLast());

                if (first instanceof SetInstruction set && set.getResult().equals(variable)) {
                    // Make sure all <variable> arguments of the last instruction are input
                    boolean replaceInputArg = last.typedArgumentsStream()
                            .filter(t -> t.argument().equals(variable))
                            .allMatch(TypedArgument::isInput);

                    if (replaceInputArg) {
                        // The first instruction merely sets up a temp variable to be used by the last instruction
                        // Replacing those arguments with the value of the set instruction
                        replaceInstruction(indices.getFirst(), createEmpty(first.getAstContext()));
                        replaceInstruction(indices.getLast(), replaceAllArgs(last, variable, set.getValue()).withContext(set.getAstContext()));
                        replaced = true;
                    }
                } else if (last instanceof SetInstruction set && set.getValue().equals(variable)) {
                    // Make sure all <variable> arguments of the other instruction are output
                    boolean replacesOutputArg = first.typedArgumentsStream()
                            .filter(t -> t.argument().equals(variable))
                            .allMatch(TypedArgument::isOutput);

                    if (replacesOutputArg) {
                        // The last instruction merely transfers a value from the output argument of the first instruction
                        // Replacing those arguments with target of the set instruction
                        replaceInstruction(indices.getFirst(), replaceAllArgs(first, variable, set.getResult()).withContext(set.getAstContext()));
                        replaceInstruction(indices.getLast(), createEmpty(last.getAstContext()));
                        replaced = true;
                    }
                }
            }
        }

        return replaced;
    }

    private void replaceUnusedOutputs(Set<LogicArgument> inputTempVariables, LogicIterator iterator) {
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
    }

    private Set<LogicArgument> gatherInputTempVariables() {
        return optimizationContext.instructionStream()
                .flatMap(LogicInstruction::inputArgumentsStream)
                .filter(LogicArgument::isTemporaryVariable)
                .collect(Collectors.toSet());
    }

    private Map<LogicArgument, SortedSet<Integer>> gatherTempVariableUses(List<LogicInstruction> program) {
        return IntStream.range(0, program.size())
                .filter(i -> !(program.get(i) instanceof PushOrPopInstruction))
                .boxed()
                .mapMulti((Integer index, Consumer<VariableIndex> consumer) -> program.get(index).inputOutputArgumentsStream()
                        .filter(LogicArgument::isTemporaryVariable)
                        .map(a -> new VariableIndex(a, index))
                        .forEach(consumer))
                .collect(Collectors.groupingBy(VariableIndex::variable,
                        Collectors.mapping(VariableIndex::index, Collectors.toCollection(TreeSet::new))));
    }

    private record VariableIndex(LogicArgument variable, int index) {}
}
