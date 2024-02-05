package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mindcode.logic.LogicVariable;

import java.util.*;
import java.util.stream.Collectors;

import static info.teksol.mindcode.compiler.generator.AstSubcontextType.*;

/**
 * This optimizer moves loop invariant code in front of given loop.
 */
public class LoopHoisting extends BaseOptimizer {
    public LoopHoisting(OptimizationContext optimizationContext) {
        super(Optimization.LOOP_HOISTING, optimizationContext);
    }

    private int count = 0;

    @Override
    public void generateFinalMessages() {
        super.generateFinalMessages();
        if (count > 0) {
            emitMessage(MessageLevel.INFO, "%6d loops improved by %s.", count, getName());
        }
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        int previous = count;
        forEachContext(AstContextType.LOOP, BASIC, loop -> {
            moveInvariants(loop);
            return null;
        });
        return count > previous;
    }

    private void moveInvariants(AstContext loop) {
        if (loop.findSubcontext(ITERATOR) != null) {
            return;
        }

        List<AstContext> parts = new ArrayList<>(loop.children());
        if (parts.get(0).matches(INIT)) {
            parts.remove(0);
        }

        int conditions = (int) parts.stream().filter(c -> c.matches(CONDITION)).count();
        if (conditions == 2) {
            if (!parts.get(0).matches(CONDITION)) return;
        } else if (conditions != 1) {
            return;
        }

        Map<LogicVariable, Set<LogicVariable>> dependencies = new HashMap<>();
        parts.stream()
                .map(this::contextInstructions)
                .flatMap(LogicList::stream)
                .forEachOrdered(ix -> addDependencies(loop, dependencies, ix));

        while (propagateDependencies(dependencies));

        // If there are function calls, all global variables are unsafe
        final boolean hasFunctionCalls = loop.containsChildContext(ctx ->
                ctx.subcontextType() == OUT_OF_LINE_CALL || ctx.subcontextType() == RECURSIVE_CALL);

        if (hasFunctionCalls) {
            List<LogicVariable> globalVariables = dependencies.values().stream()
                    .flatMap(Set::stream)
                    .filter(LogicVariable::isGlobalVariable)
                    .toList();

            globalVariables.forEach(v -> dependencies.computeIfAbsent(v, w -> new HashSet<>()).add(v));
        }

        // Dependencies now maps a variable to a full set of variables it depends on, directly or indirectly

        // Primary loop variables: depend on themselves
        Set<LogicVariable> primary = dependencies.entrySet().stream()
                .filter(e -> e.getValue().contains(e.getKey()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        // Secondary loop variables: depend on primary loop variables
        Set<LogicVariable> loopVariables = dependencies.entrySet().stream()
                .filter(e -> e.getValue().stream().anyMatch(primary::contains))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        // An instruction is invariant if none of its arguments (input, output) is a loop variable
        // Only move instructions from the direct children
        // Invariant if/loop expressions would need specific handling
        List<LogicInstruction> invariants = parts.stream().flatMap(child -> contextStream(child)
                .filter(ix -> safeToMove(loop, ix))
                .filter(ix -> ix.inputOutputArgumentsStream().noneMatch(loopVariables::contains))
        ).toList();

        if (!invariants.isEmpty()) {
            AstContext initContext = getInitContext(loop);
            LogicList instructions = buildLogicList(initContext,
                    invariants.stream().map(ix -> ix.withContext(initContext)).toList());

            int index = firstInstructionIndex(parts.get(0));
            insertInstructions(index, instructions);
            invariants.forEach(this::removeInstruction);
            count++;
        }
    }

    private void addDependencies(AstContext loop, Map<LogicVariable, Set<LogicVariable>> dependencies, LogicInstruction ix) {
        if (instructionProcessor.isSafe(ix) && instructionProcessor.isDeterministic(ix)
                && loop.executesOnce(ix)) {
            List<LogicVariable> inputs = ix.inputArgumentsStream()
                    .filter(LogicVariable.class::isInstance)
                    .map(LogicVariable.class::cast)
                    .toList();

            ix.outputArgumentsStream()
                    .filter(LogicVariable.class::isInstance)
                    .map(LogicVariable.class::cast)
                    .forEach(arg -> dependencies.computeIfAbsent(arg, a -> new HashSet<>()).addAll(inputs));
        } else {
            // Add output variables as depending on themselves, removing their invariant status
            ix.outputArgumentsStream()
                    .filter(LogicVariable.class::isInstance)
                    .map(LogicVariable.class::cast)
                    .forEach(arg -> dependencies.computeIfAbsent(arg, a -> new HashSet<>()).add(arg));
        }
    }

    private boolean propagateDependencies(Map<LogicVariable, Set<LogicVariable>> dependencies) {
        boolean modified = false;
        for (LogicVariable variable : dependencies.keySet()) {
            Set<LogicVariable> variableDependencies = dependencies.get(variable);
            for (LogicVariable v : List.copyOf(variableDependencies)) {
                if (!v.equals(variable)) {
                    Set<LogicVariable> newDependencies = dependencies.get(v);
                    if (newDependencies != null && variableDependencies.addAll(newDependencies)) {
                        modified = true;
                    }
                }
            }
        }

        return modified;
    }

    private AstContext getInitContext(AstContext loop) {
        AstContext ctx = loop.findSubcontext(INIT);
        return ctx == null ? loop.createSubcontext(INIT, 1.0) : ctx;
    }

    private boolean safeToMove(AstContext loop, LogicInstruction instruction) {
        return instructionProcessor.isDeterministic(instruction)
                && instructionProcessor.isSafe(instruction)
                && loop.executesOnce(instruction);
    }
}
