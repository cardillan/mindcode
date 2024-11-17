package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.GenerationGoal;
import info.teksol.mindcode.compiler.generator.GeneratorOutput;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.NoOpInstruction;
import info.teksol.util.TraceFile;

import java.util.*;
import java.util.function.Consumer;

import static info.teksol.mindcode.compiler.optimization.OptimizationPhase.*;

public class OptimizationCoordinator {
    public static final boolean TRACE = false;
    public static final boolean DEBUG_PRINT = TRACE;

    public static final boolean IGNORE_UNINITIALIZED = false;

    private final List<LogicInstruction> program = new ArrayList<>();
    private final InstructionProcessor instructionProcessor;
    private final Consumer<MindcodeMessage> messageRecipient;
    private final CompilerProfile profile;
    private DebugPrinter debugPrinter = new NullDebugPrinter();
    private OptimizationContext optimizationContext;

    public OptimizationCoordinator(InstructionProcessor instructionProcessor, CompilerProfile profile,
            Consumer<MindcodeMessage> messageRecipient) {
        this.instructionProcessor = instructionProcessor;
        this.messageRecipient = messageRecipient;
        this.profile = profile;
    }

    public static boolean isDebugOn() {
        return TRACE || DEBUG_PRINT || IGNORE_UNINITIALIZED;
    }

    public void setDebugPrinter(DebugPrinter debugPrinter) {
        this.debugPrinter = debugPrinter;
    }

    protected Map<Optimization, Optimizer> createOptimizers() {
        Map<Optimization, Optimizer> result = new LinkedHashMap<>();
        for (Optimization optimization : Optimization.LIST) {
            OptimizationLevel level = profile.getOptimizationLevel(optimization);
            if (level != OptimizationLevel.NONE) {
                Optimizer optimizer = optimization.getInstanceCreator().apply(optimizationContext);
                optimizer.setMessageRecipient(messageRecipient);
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

    public List<LogicInstruction> optimize(GeneratorOutput generatorOutput) {
        program.addAll(generatorOutput.instructions());

        try (TraceFile traceFile = new TraceFile(TRACE, DEBUG_PRINT)) {
            optimizationContext = new OptimizationContext(traceFile, profile, instructionProcessor, program,
                    generatorOutput.callGraph(), generatorOutput.rootAstContext());

            int count = program.stream().mapToInt(LogicInstruction::getRealSize).sum();
            messageRecipient.accept(OptimizerMessage.info("%6d instructions before optimizations.", count));

            debugPrinter.registerIteration(null, "", List.copyOf(program));

            Map<Optimization, Optimizer> optimizers = createOptimizers();

            optimizePhase(INITIAL, optimizers, 0, generatorOutput);
            boolean modified = true;
            for (int pass = 1; modified && pass <= profile.getOptimizationPasses(); pass++) {
                modified = optimizePhase(ITERATED, optimizers, pass, generatorOutput);
            }
            if (modified) {
                messageRecipient.accept(OptimizerMessage.warn("Optimization passes limit (%d) reached.", profile.getOptimizationPasses()));
            }
            optimizePhase(FINAL, optimizers, 0, generatorOutput);

            optimizers.values().forEach(Optimizer::generateFinalMessages);
            int newCount = program.stream().mapToInt(LogicInstruction::getRealSize).sum();
            messageRecipient.accept(OptimizerMessage.info("%6d instructions after optimizations.", newCount));

            if (modified) {
                messageRecipient.accept(OptimizerMessage.warn("\nOptimization passes limited at %d.",
                        profile.getOptimizationPasses()));
            }

            optimizationContext.removeInactiveInstructions();
            optimizationStatistics.forEach(messageRecipient);

            return List.copyOf(program);
        }
    }

    private boolean optimizePhase(OptimizationPhase phase, Map<Optimization, Optimizer> optimizers, int pass, GeneratorOutput generatorOutput) {
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

    private void outputPossibleOptimization(OptimizationAction opt, int costLimit, OptimizationAction selected, int difference) {
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
