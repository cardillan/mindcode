package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicBoolean;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static info.teksol.mc.mindcode.compiler.astcontext.AstContextType.*;
import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.*;

/// This optimizer moves loop invariant code in front of a given loop.
@NullMarked
class LoopHoisting extends BaseOptimizer {
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
        optimizationContext.clearLoopInvariants();
        forEachContext(c -> c.matches(LOOP, EACH) && c.matches(BASIC), loop -> {
            moveInvariants(loop);
            return null;
        });
        return count > previous;
    }

    private void moveInvariants(AstContext loop) {
        List<AstContext> parts = new ArrayList<>(loop.children());
        while (!parts.isEmpty() && parts.getFirst().matches(INIT, HOIST)) {
            parts.removeFirst();
        }

        int conditions = (int) parts.stream().filter(c -> c.matches(CONDITION)).count();
        if (conditions == 0) {
            // This looks like a list iteration loop. Remove all ITR_LEADING and ITR_TRAILING contexts;
            // if none is found, it wasn't a list iterator loop, we quit as we don't understand the structure.
            if (!parts.removeIf(c -> c.matches(ITR_LEADING, ITR_TRAILING))) {
                return;
            }
        } else if (conditions == 2) {
            if (!parts.getFirst().matches(CONDITION)) return;
        } else if (conditions != 1) {
            return;
        }

        boolean nestedLoop = loop.existingParent().findSuperContextOfType(
                ctx -> ctx.matches(IF, CASE, LOOP, EACH))
                .filter(ctx -> ctx.matches(LOOP, EACH))
                .isPresent();

        boolean guaranteedToRun = !hasEntryCondition(loop)
                || optimizationContext.evaluateLoopEntryCondition(parts.getFirst()) == LogicBoolean.TRUE;

        Set<LogicArgument> loopVariables = findLoopVariables(loop, parts);

        // Only move instructions from the direct children
        Set<LogicArgument> readVariables = new HashSet<>();
        List<LogicInstruction> isolatedInvariants = new ArrayList<>();
        List<LogicInstruction> generalInvariants = new ArrayList<>();

        parts.stream()
                .filter(c -> !c.matches(HOIST))
                .flatMap(this::contextStream)
                .filter(ix -> !(ix instanceof EmptyInstruction))
                .forEach(ix -> {
                    // An instruction can be hoisted if none of its arguments (input, output) is a loop variable,
                    // and it doesn't modify a variable that has already been read by the loop
                    if (safeToMove(loop, ix)
                            && ix.inputOutputArgumentsStream().noneMatch(loopVariables::contains)
                            && ix.outputArgumentsStream().noneMatch(readVariables::contains)
                    ) {
                        if (guaranteedToRun || nestedLoop && ix.getAllWrites().noneMatch(arg -> nonlocal(loop, arg))) {
                            isolatedInvariants.add(ix);
                        } else {
                            generalInvariants.add(ix);
                        }
                    }

                    // As soon as a variable is read by the loop, it cannot be safely hoisted
                    ix.inputArgumentsStream().filter(a -> a instanceof LogicVariable).forEach(readVariables::add);
                    readVariables.addAll(ix.getSideEffects().reads());
                    if (ix instanceof CallInstruction || ix instanceof CallRecInstruction) {
                        MindcodeFunction function = ix.getAstContext().function();
                        if (function != null) {
                            readVariables.addAll(optimizationContext.getFunctionReads(function));
                        }
                    }
                });

        if (!generalInvariants.isEmpty() && loop.findSubcontext(HOIST) == null) {
            optimizationContext.storeLoopInvariants(loop, List.copyOf(generalInvariants));
            generalInvariants.clear();
        }

        moveInvariants(loop, isolatedInvariants, INIT);
        moveInvariants(loop, generalInvariants, HOIST);
    }

    private boolean nonlocal(AstContext loop, LogicArgument arg) {
        return arg instanceof LogicVariable var &&
                optimizationContext.getVariableReferences(var).stream().anyMatch(ix -> !ix.belongsTo(loop));
    }

    private void moveInvariants(AstContext loop, List<LogicInstruction> invariants, AstSubcontextType type) {
        if (invariants.isEmpty()) return;

        AstContext context = getHoistContext(loop, type);
        LogicList instructions = buildLogicList(context,
                invariants.stream().map(ix -> ix.withContext(context)).toList());
        instructions.stream().filter(ix -> ix.getHoistId() != LogicLabel.EMPTY).forEach(ix -> ix.setHoisted(true));

        int index = loop.children().getLast() == context
            // The context has just been added
            ? firstInstructionIndex(type == INIT ? loop : Objects.requireNonNull(loop.findSubcontext(BODY)))
            : lastInstructionIndex(context) + (type == INIT ? 1 : 0);

        insertInstructions(index, instructions);
        invariants.forEach(this::invalidateInstruction);
        count++;
    }

    private Set<LogicArgument> findLoopVariables(AstContext loop, List<AstContext> parts) {
        Map<LogicVariable, Set<LogicVariable>> dependencies = new HashMap<>();
        Map<LogicVariable, Set<LogicVariable>> dependants = new HashMap<>();
        parts.stream()
                .map(this::contextInstructions)
                .flatMap(LogicList::stream)
                .forEachOrdered(ix -> addDependencies(loop, dependencies, dependants, ix));

        propagateAllDependencies(dependencies);

        // Handle variables modified by functions called from this loop
        Map<MindcodeFunction, Set<LogicVariable>> functionWrites = optimizationContext.getAllFunctionWrites();
        Set<LogicVariable> modifiedVariables = optimizationContext
                .contexts(loop,   ctx -> ctx.subcontextType() == OUT_OF_LINE_CALL || ctx.subcontextType() == RECURSIVE_CALL)
                .stream()
                .map(AstContext::function)
                .filter(Objects::nonNull)
                .filter(f -> !f.equals(loop.function()))
                .filter(functionWrites::containsKey)
                .flatMap(f -> functionWrites.get(f).stream())
                .collect(Collectors.toSet());

        modifiedVariables.forEach(v -> dependencies.computeIfAbsent(v, _ -> new HashSet<>()).add(v));

        // Handle side effects!
        Set<LogicVariable> sideEffectModifications = optimizationContext
                .instructionStream()
                .filter(ix -> ix.getSideEffects() != SideEffects.NONE)
                .<LogicVariable>mapMulti((ix, consumer) -> ix.getSideEffects().apply(_ -> {}, consumer, consumer))
                .collect(Collectors.toSet());

        sideEffectModifications.forEach(v -> dependencies.computeIfAbsent(v, _ -> new HashSet<>()).add(v));

        // All variables generated in ITR_LEADING contexts are loop variables
        List<LogicVariable> iteratorVariables = loop.children().stream()
                .filter(ctx -> ctx.matches(ITR_LEADING))
                .flatMap(this::contextStream)
                .flatMap(LogicInstruction::outputArgumentsStream)
                .map(LogicVariable.class::cast)
                .toList();

        iteratorVariables.forEach(v -> dependencies.computeIfAbsent(v, _ -> new HashSet<>()).add(v));

        // The 'dependencies' map now maps a variable to a full set of variables it depends on, directly or indirectly

        // Primary loop variables: depend on themselves
        Set<LogicVariable> primary = dependencies.entrySet().stream()
                .filter(e -> e.getValue().contains(e.getKey()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        // Secondary loop variables: depend on primary loop variables
        return dependencies.entrySet().stream()
                .filter(e -> e.getValue().stream().anyMatch(primary::contains))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private boolean isSafe(LogicInstruction instruction) {
        return instruction.isSafe() && instruction.getOpcode() != Opcode.JUMP && instruction.outputArgumentsStream().findAny().isPresent();
    }

    private static final Function<LogicVariable, Set<LogicVariable>> createHashSet = _ -> new HashSet<>();

    private void addDependencies(AstContext loop, Map<LogicVariable, Set<LogicVariable>> dependencies,
            Map<LogicVariable, Set<LogicVariable>> dependants, LogicInstruction instruction) {

        // When dependencies.get(x).contain(y) is true, it means that x depends on y.
        // When dependants.get(x).contains(y) is true, it means that y depends on x.

        if (isSafe(instruction) && instructionProcessor.isDeterministic(instruction) && (loop.executesOnce(instruction))) {
            List<LogicVariable> inputs = instruction.inputArgumentsStream()
                    .filter(LogicVariable.class::isInstance)
                    .map(LogicVariable.class::cast)
                    .toList();

            instruction.outputArgumentsStream()
                    .filter(LogicVariable.class::isInstance)
                    .map(LogicVariable.class::cast)
                    .forEach(output -> {
                        if (dependencies.containsKey(output) && (!output.isTemporaryVariable()
                                || optimizationContext.getVariableReferences(output).size() > 1)) {
                            dependencies.get(output).add(output);
                        } else {
                            dependencies.computeIfAbsent(output, createHashSet).addAll(inputs);
                        }

                        dependants.getOrDefault(output, Set.of())
                                .stream()
                                .filter(dependencies::containsKey)
                                .forEach(arg -> dependencies.computeIfAbsent(arg, createHashSet).add(arg));

                        if (output.isVolatile()) {
                            dependencies.computeIfAbsent(output, createHashSet).add(output);
                            dependants.computeIfAbsent(output, createHashSet).add(output);
                        }

                        inputs.forEach(input -> dependants.computeIfAbsent(input, createHashSet).add(output));
                    });
        } else {
            // This instruction isn't loop-independent: it's unsafe, nondeterministic or nonlinear.
            // Add output variables as depending on themselves, removing their invariant status
            instruction.outputArgumentsStream()
                    .filter(LogicVariable.class::isInstance)
                    .map(LogicVariable.class::cast)
                    .forEach(arg -> dependencies.computeIfAbsent(arg, createHashSet).add(arg));
        }
    }

    private AstContext getHoistContext(AstContext loop, AstSubcontextType type) {
        AstContext ctx = loop.findSubcontext(type);
        return ctx == null ? loop.createSubcontext(type, 1.0) : ctx;
    }

    private boolean isMovable(LogicInstruction instruction) {
        return instructionProcessor.isDeterministic(instruction) && isSafe(instruction);
    }

    private boolean safeToMove(AstContext loop, LogicInstruction instruction) {
        return isMovable(instruction) && loop.executesOnce(instruction);
    }
}
