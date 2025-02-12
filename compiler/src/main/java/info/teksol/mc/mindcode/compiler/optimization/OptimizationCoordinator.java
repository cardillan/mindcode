package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.NoOpInstruction;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GenerationGoal;
import info.teksol.mc.util.TraceFile;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static info.teksol.mc.mindcode.compiler.optimization.OptimizationPhase.*;

@NullMarked
public class OptimizationCoordinator {
    public static final boolean TRACE = false;
    public static final boolean DEBUG_PRINT = TRACE;
    public static final boolean SYSTEM_OUT = false;

    public static final boolean IGNORE_UNINITIALIZED = false;

    private final List<LogicInstruction> program = new ArrayList<>();
    private final InstructionProcessor instructionProcessor;
    private final MessageConsumer messageConsumer;
    private final CompilerProfile profile;
    private DebugPrinter debugPrinter = new NullDebugPrinter();
    private @Nullable OptimizationContext optimizationContext;

    public OptimizationCoordinator(InstructionProcessor instructionProcessor, CompilerProfile profile,
            MessageConsumer messageConsumer) {
        this.instructionProcessor = instructionProcessor;
        this.messageConsumer = messageConsumer;
        this.profile = profile;
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
            OptimizationLevel level = profile.getOptimizationLevel(optimization);
            if (level != OptimizationLevel.NONE) {
                Optimizer optimizer = optimization.getInstanceCreator().apply(optimizationContext);
                optimizer.setMessageRecipient(messageConsumer);
                optimizer.setDebugPrinter(debugPrinter);

                optimizer.setLevel(level);
                optimizer.setMemoryModel(profile.getMemoryModel());
                optimizer.setGoal(profile.getGoal());

                result.put(optimization, optimizer);
            }
        }

        return result;
    }

    private final List<MindcodeMessage> optimizationStatistics = new ArrayList<>();

    public List<LogicInstruction> optimize(CallGraph callGraph, List<LogicInstruction> instructions, AstContext rootAstContext) {
        program.addAll(instructions);

        try (TraceFile traceFile = TraceFile.createTraceFile(TRACE, DEBUG_PRINT, SYSTEM_OUT)) {
            optimizationContext = new OptimizationContext(traceFile, profile, instructionProcessor, program,
                    callGraph, rootAstContext);

            int count = program.stream().mapToInt(LogicInstruction::getRealSize).sum();
            messageConsumer.accept(OptimizerMessage.info("%6d instructions before optimizations.", count));

            debugPrinter.registerIteration(null, "", List.copyOf(program));

            Map<Optimization, Optimizer> optimizers = createOptimizers();

            optimizePhase(INITIAL, optimizers, 0);
            boolean modified = true;
            for (int pass = 1; modified && pass <= profile.getOptimizationPasses(); pass++) {
                modified = optimizePhase(ITERATED, optimizers, pass);
            }
            if (modified) {
                messageConsumer.accept(OptimizerMessage.warn(WARN.OPTIMIZATION_PASSES_LIMIT_REACHED, profile.getOptimizationPasses()));
            }
            optimizePhase(FINAL, optimizers, 0);

            optimizers.values().forEach(Optimizer::generateFinalMessages);
            int newCount = program.stream().mapToInt(LogicInstruction::getRealSize).sum();
            messageConsumer.accept(OptimizerMessage.info("%6d instructions after optimizations.", newCount));

            if (modified) {
                messageConsumer.accept(OptimizerMessage.warn(WARN.OPTIMIZATION_PASSES_LIMITED,
                        profile.getOptimizationPasses()));
            }

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
            if (phase == FINAL) {
                try (OptimizationContext.LogicIterator it = optimizationContext.createIterator()) {
                    while (it.hasNext()) {
                        if (it.next() instanceof NoOpInstruction) {
                            it.remove();
                        }
                    }
                }
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
            int costLimit = profile.getGoal() == GenerationGoal.SIZE ? 0 : Math.max(0, profile.getInstructionLimit() - initialSize);
            int expandedCostLimit = profile.getGoal() == GenerationGoal.SIZE ? 0 : 500 + costLimit;

            optimizationContext.prepare();
            List<OptimizationAction> possibleOptimizations = phase.optimizations.stream()
                    .map(optimizers::get)
                    .filter(Objects::nonNull)
                    .flatMap(o -> o.getPossibleOptimizations(expandedCostLimit).stream())
                    .toList();
            optimizationContext.finish();

            if (possibleOptimizations.isEmpty()) {
                break;
            }

            OptimizationAction selectedAction = possibleOptimizations.stream()
                    .filter(a -> a.cost() <= costLimit)
                    .max(ACTION_COMPARATOR).orElse(null);
            if (selectedAction != null) {
                optimizationContext.prepare();
                OptimizationResult result = selectedAction.apply(costLimit);
                optimizationContext.finish();

                if (result == OptimizationResult.REALIZED) {
                    Optimizer optimizer = optimizers.get(Optimization.DATA_FLOW_OPTIMIZATION);
                    if (optimizer != null) {
                        optimizer.optimize(phase, pass);
                    }
                    modified = true;
                }
            }

            if (profile.getGoal() != GenerationGoal.SIZE) {
                int difference = codeSize() - initialSize;
                optimizationStatistics.add(OptimizerMessage.debug(
                        "\nPass %d: speed optimization selection (cost limit %d):", pass, costLimit));
                possibleOptimizations.forEach(t -> outputPossibleOptimization(t, costLimit, selectedAction, difference));
            }

            if (selectedAction == null) {
                break;
            }
        }

        return modified;
    }

    private int codeSize() {
        return program.stream().mapToInt(LogicInstruction::getRealSize).sum();
    }

    private static final Comparator<OptimizationAction> ACTION_COMPARATOR =
            Comparator.comparingDouble(OptimizationAction::efficiency)
                    .thenComparing(Comparator.comparingInt(OptimizationAction::cost).reversed());

    private void outputPossibleOptimization(OptimizationAction opt, int costLimit, @Nullable OptimizationAction selected, int difference) {
        OptimizerMessage message;
        if (opt == selected) {
            message = OptimizerMessage.debug("  * %-60s cost %5d, benefit %10.1f, efficiency %10.1f (%+d instructions)",
                    opt, opt.cost(), opt.benefit(), opt.efficiency(), difference);
        } else {
            message = OptimizerMessage.debug("  %s %-60s cost %5d, benefit %10.1f, efficiency %10.1f",
                    opt.cost() > costLimit ? "!" : " ", opt, opt.cost(), opt.benefit(), opt.efficiency());
        }
        optimizationStatistics.add(message);
    }
}
