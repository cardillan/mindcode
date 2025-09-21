package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.InstructionCounter;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.generation.variables.OptimizerContext;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionArrayExpander;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GenerationGoal;
import info.teksol.mc.util.TraceFile;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

import static info.teksol.mc.mindcode.compiler.optimization.OptimizationPhase.*;

@NullMarked
public class OptimizationCoordinator {
    public static final boolean TRACE = false;
    public static final boolean DEBUG_PRINT = TRACE;
    public static final boolean TRACE_ALL = true;
    public static final boolean SYSTEM_OUT = false;

    public static final boolean IGNORE_UNINITIALIZED = false;

    private final List<LogicInstruction> program = new ArrayList<>();
    private final InstructionProcessor instructionProcessor;
    private final MessageConsumer messageConsumer;
    private final OptimizerContext optimizerContext;
    private final LogicInstructionArrayExpander arrayExpander;
    private final boolean remoteLibrary;
    private final CompilerProfile globalProfile;
    private DebugPrinter debugPrinter = new NullDebugPrinter();
    private @Nullable OptimizationContext optimizationContext;

    public OptimizationCoordinator(InstructionProcessor instructionProcessor, CompilerProfile globalProfile,
            MessageConsumer messageConsumer, OptimizerContext optimizerContext, LogicInstructionArrayExpander arrayExpander,
            boolean remoteLibrary) {
        this.instructionProcessor = instructionProcessor;
        this.messageConsumer = messageConsumer;
        this.optimizerContext = optimizerContext;
        this.globalProfile = globalProfile;
        this.arrayExpander = arrayExpander;
        this.remoteLibrary = remoteLibrary;
    }

    public static boolean isDebugOn() {
        return TRACE || DEBUG_PRINT || IGNORE_UNINITIALIZED;
    }

    public void setDebugPrinter(DebugPrinter debugPrinter) {
        this.debugPrinter = debugPrinter;
    }

    protected Map<Optimization, Optimizer> createOptimizers() {
        assert optimizationContext != null;

        Map<Optimization, Optimizer> result = new LinkedHashMap<>();
        for (Optimization optimization : Optimization.LIST) {
            OptimizationLevel level = globalProfile.getOptimizationLevel(optimization);
            if (level != OptimizationLevel.NONE) {
                Optimizer optimizer = optimization.getInstanceCreator().apply(optimizationContext);
                optimizer.setDebugPrinter(debugPrinter);
                optimizer.setLevel(level);
                result.put(optimization, optimizer);
            }
        }

        return result;
    }

    private final List<MindcodeMessage> optimizationStatistics = new ArrayList<>();

    public List<LogicInstruction> optimize(CallGraph callGraph, List<LogicInstruction> instructions, AstContext rootAstContext) {
        program.addAll(instructions);
        boolean passesExceeded = false;

        try (TraceFile traceFile = TraceFile.createTraceFile(TRACE, DEBUG_PRINT, SYSTEM_OUT)) {
            optimizationContext = new OptimizationContext(traceFile, messageConsumer, globalProfile, instructionProcessor,
                    optimizerContext, program, callGraph, rootAstContext, remoteLibrary);

            int count = codeSize();
            messageConsumer.accept(OptimizerMessage.info("%6d instructions before optimizations.", count));

            debugPrinter.registerIteration(null, "", List.copyOf(program));

            Map<Optimization, Optimizer> optimizers = createOptimizers();

            optimizePhase(INITIAL, optimizers, 0);

            int pass = 1;
            boolean modified = true;

            // We reserve one pass for the phase after the expansion
            while (modified && pass < globalProfile.getOptimizationPasses()) {
                modified = optimizePhase(ITERATED, optimizers, pass++);
            }

            if (arrayExpander.analyze(program)) {
                for (int index = 0; index < program.size(); index++) {
                    if (program.get(index) instanceof ArrayAccessInstruction ix) {
                        List<LogicInstruction> expanded = arrayExpander.expand(ix);
                        optimizationContext.removeInstruction(index);
                        for (LogicInstruction instruction : expanded) {
                            optimizationContext.insertInstructionUnchecked(index++, instruction);
                        }
                        index--;
                    }
                }

                boolean generateEndSeparator = program.getLast().getOpcode() == Opcode.END;
                for (LogicInstruction instruction : arrayExpander.getJumpTables(generateEndSeparator)) {
                    optimizationContext.insertInstructionUnchecked(program.size(), instruction);
                }
                modified = true;

                debugPrinter.registerIteration(null, "Virtual Instruction Expansion", List.copyOf(program));
            }

            while (modified && pass <= globalProfile.getOptimizationPasses()) {
                modified = optimizePhase(ITERATED, optimizers, pass++);
            }

            passesExceeded = modified;

            do {
                modified = optimizePhase(JUMPS, optimizers, pass++);
            } while (modified && pass <= globalProfile.getOptimizationPasses());

            if (passesExceeded || modified) {
                messageConsumer.accept(OptimizerMessage.warn(WARN.OPTIMIZATION_PASSES_LIMIT_REACHED, globalProfile.getOptimizationPasses()));
            }

            optimizePhase(FINAL, optimizers, 0);

            optimizers.values().forEach(Optimizer::generateFinalMessages);
            int newCount = codeSize();
            messageConsumer.accept(OptimizerMessage.info("%6d instructions after optimizations.", newCount));

            optimizationContext.removeInactiveInstructions();
            optimizationStatistics.forEach(messageConsumer);

            optimizationContext.outputUninitializedVariables(messageConsumer);

            return List.copyOf(program);
        }
    }

    private boolean optimizePhase(OptimizationPhase phase, Map<Optimization, Optimizer> optimizers, int pass) {
        assert optimizationContext != null;

        boolean modified = false;
        for (Optimization optimization : phase.optimizations) {
            if (phase.breaksContextStructure()) {
                optimizationContext.rebuildLabelReferences();
                optimizationContext.removeInactiveInstructions();
            }

            Optimizer optimizer = optimizers.get(optimization);
            if (optimizer != null) {
                if (optimizer.optimize(phase, pass)) modified = true;
            }
        }

        if (phase != ITERATED) {
            return modified;
        }

        while (true) {
            int initialSize = codeSize();
            int costLimit = Math.max(0, globalProfile.getInstructionLimit() - initialSize);
            int expandedCostLimit = 500 + costLimit;

            optimizationContext.prepare();
            List<OptimizationAction> possibleOptimizations = phase.optimizations.stream()
                    .map(optimizers::get)
                    .filter(Objects::nonNull)
                    .flatMap(o -> o.getPossibleOptimizations(expandedCostLimit).stream())
                    .filter(this::acceptOptimization)
                    .toList();
            optimizationContext.finish();

            if (possibleOptimizations.isEmpty()) {
                break;
            }

            GenerationGoal goal = possibleOptimizations.stream().map(OptimizationAction::goal).min(Comparator.naturalOrder()).get();

            Set<OptimizationAction> considered = Collections.newSetFromMap(new IdentityHashMap<>());
            OptimizationAction selectedAction = selectAction(goal, possibleOptimizations, costLimit, considered);
            if (selectedAction != null) {
                optimizationContext.prepare();
                optimizationContext.debugPrintProgram(String.format("%n*** Performing optimization %s ***%n", selectedAction), true);
                OptimizationResult result = selectedAction.apply(costLimit);
                optimizationContext.finish();

                if (result == OptimizationResult.REALIZED) {
                    optimizationContext.debugPrintProgram(String.format("%n*** Performing Data Flow optimization after %s ***%n", selectedAction), true);
                    Optimizer optimizer = optimizers.get(Optimization.DATA_FLOW_OPTIMIZATION);
                    if (optimizer != null) {
                        optimizer.optimize(phase, pass);
                    }
                    modified = true;
                } else {
                    instructionProcessor.error(SourcePosition.EMPTY, ERR.INTERNAL_ERROR_OPTIMIZER_ACTION,
                            selectedAction.optimization().getName(), selectedAction.optimization().getOptionName());
                    throw new MindcodeInternalError("Error applying dynamic optimization: " + selectedAction);
                }
            }

            int difference = codeSize() - initialSize;
            optimizationStatistics.add(OptimizerMessage.debug(
                    "\nPass %d: %s optimization selection (cost limit %d):", pass, goal.toString().toLowerCase(), costLimit));
            possibleOptimizations.forEach(t -> outputPossibleOptimization(t, costLimit, selectedAction, difference, considered));

            if (selectedAction == null) {
                break;
            }
        }

        return modified;
    }

    private boolean acceptOptimization(OptimizationAction action) {
        return switch (action.goal()) {
            case SIZE    -> action.cost() < 0;
            case NEUTRAL -> action.cost() <= 0 && action.benefit() >= 0 && (action.cost() < 0 || action.benefit() > 0);
            case SPEED   -> action.benefit() > 0;
        };
    }

    /// Selects an optimization action, taking action groups into account.
    private @Nullable OptimizationAction selectAction(GenerationGoal goal, List<OptimizationAction> possibleOptimizations,
            int costLimit, Set<OptimizationAction> considered) {
        return switch (goal) {
            case SIZE -> selectActionForSize(possibleOptimizations);
            case NEUTRAL -> selectActionNeutral(possibleOptimizations);
            case SPEED -> selectActionForSpeed(possibleOptimizations, costLimit, considered);
        };
    }

    private static final Comparator<OptimizationAction> SIZE_GOAL_COMPARATOR =
            Comparator.comparingInt(OptimizationAction::cost)
                    .thenComparing(Comparator.comparingDouble(OptimizationAction::benefit).reversed());

    /// Selects an optimization action, taking action groups into account.
    private @Nullable OptimizationAction selectActionForSize(List<OptimizationAction> possibleOptimizations) {
        return possibleOptimizations.stream().filter(a -> a.goal() == GenerationGoal.SIZE).min(SIZE_GOAL_COMPARATOR).orElse(null);
    }

    // We're maximizing the effect of the optimization: instructions saved times execution benefit.
    private static final Comparator<OptimizationAction> NEUTRAL_GOAL_COMPARATOR =
            Comparator.comparingDouble(a -> -a.cost() * a.benefit());

    /// Selects an optimization action, taking action groups into account.
    private @Nullable OptimizationAction selectActionNeutral(List<OptimizationAction> possibleOptimizations) {
        return possibleOptimizations.stream().filter(a -> a.goal() == GenerationGoal.NEUTRAL).max(NEUTRAL_GOAL_COMPARATOR).orElse(null);
    }

    private static final Comparator<OptimizationAction> SPEED_GOAL_COMPARATOR =
            Comparator.comparingDouble(OptimizationAction::speedEfficiency).reversed()
                    .thenComparingInt(OptimizationAction::cost);

    /// Selects an optimization action, taking action groups into account.
    private @Nullable OptimizationAction selectActionForSpeed(List<OptimizationAction> possibleOptimizations, int costLimit, Set<OptimizationAction> considered) {
        List<OptimizationAction> actions = possibleOptimizations.stream().filter(a -> a.goal() == GenerationGoal.SPEED).sorted(SPEED_GOAL_COMPARATOR).toList();

        // Last considered action cost per group
        Map<String, Integer> actionCosts = new HashMap<>();
        OptimizationAction candidate = null;

        for (OptimizationAction action : actions) {
            int effectiveLimit = costLimit + actionCosts.getOrDefault(action.getGroup(), 0);
            if (action.cost() > effectiveLimit) continue;

            if (action.getGroup() == null) {
                return action;
            } else if (candidate == null || candidate.getGroup().equals(action.getGroup())) {
                if (candidate == null || action.benefit() > candidate.benefit()) {
                    // The action in this group has a lower efficiency but greater benefit
                    candidate = action;
                }
            }

            costLimit = costLimit - action.cost() + actionCosts.getOrDefault(action.getGroup(), 0);
            actionCosts.put(action.getGroup(), action.cost());
            considered.add(action);
        }
        return candidate;
    }

    private int codeSize() {
        return InstructionCounter.globalSize(program);
    }

    private void outputPossibleOptimization(OptimizationAction opt, int costLimit, @Nullable OptimizationAction selected, int difference,
            Set<OptimizationAction> considered) {
        Function<OptimizationAction, Double> efficiency = switch (opt.goal()) {
            case SIZE -> OptimizationAction::sizeEfficiency;
            case NEUTRAL -> OptimizationAction::neutralEfficiency;
            case SPEED -> OptimizationAction::speedEfficiency;
        };

        if (opt == selected) {
            optimizationStatistics.add(
                    OptimizerMessage.debug("  * %-60s size %+5d, benefit %10.1f, efficiency %10.3f (%+d instructions)",
                            opt, opt.cost(), opt.benefit(), efficiency.apply(opt), difference));
        } else {
            optimizationStatistics.add(
                    OptimizerMessage.debug("  %s %-60s size %+5d, benefit %10.1f, efficiency %10.3f",
                            opt.cost() > costLimit ? "!" : considered.contains(opt) ? "o" : " ",
                            opt, opt.cost(), opt.benefit(), efficiency.apply(opt)));
        }
    }
}
