package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.GenerationGoal;
import info.teksol.mindcode.compiler.MindcodeMessage;
import info.teksol.mindcode.compiler.generator.GeneratorOutput;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;

import java.util.*;
import java.util.function.Consumer;

import static info.teksol.mindcode.compiler.optimization.OptimizationPhase.*;

public class OptimizationCoordinator {
    private final List<LogicInstruction> program = new ArrayList<>();
    private final InstructionProcessor instructionProcessor;
    private final Consumer<CompilerMessage> messageRecipient;
    private final CompilerProfile profile;
    private DebugPrinter debugPrinter = new NullDebugPrinter();
    private OptimizationContext optimizationContext;

    public OptimizationCoordinator(InstructionProcessor instructionProcessor, CompilerProfile profile,
            Consumer<CompilerMessage> messageRecipient) {
        this.instructionProcessor = instructionProcessor;
        this.messageRecipient = messageRecipient;
        this.profile = profile;
    }

    public void setDebugPrinter(DebugPrinter debugPrinter) {
        this.debugPrinter = debugPrinter;
    }

    protected Map<Optimization, Optimizer> createOptimizers() {
        Map<Optimization, Optimizer> result = new LinkedHashMap<>();
        for (Optimization optimization : Optimization.LIST) {
            OptimizationLevel level = profile.getOptimizationLevel(optimization);
            if (level != OptimizationLevel.OFF) {
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

    private final List<CompilerMessage> optimizationStatistics = new ArrayList<>();

    public List<LogicInstruction> optimize(GeneratorOutput generatorOutput) {
        program.addAll(generatorOutput.instructions());

        optimizationContext = new OptimizationContext(instructionProcessor, program,
                generatorOutput.callGraph(), generatorOutput.rootAstContext());

        int count = program.stream().mapToInt(LogicInstruction::getRealSize).sum();
        messageRecipient.accept(MindcodeMessage.info("%6d instructions before optimizations.", count));

        debugPrinter.registerIteration(null, "", List.copyOf(program));

        Map<Optimization, Optimizer> optimizers = createOptimizers();

        optimizePhase(INITIAL, optimizers, 0, generatorOutput);
        boolean modified = false;
        for (int pass = 1; pass <= profile.getOptimizationPasses(); pass++) {
            modified = optimizePhase(ITERATED, optimizers, pass, generatorOutput);
            if (!modified) {
                break;
            }
        }
        optimizePhase(FINAL, optimizers, 0, generatorOutput);

        optimizers.values().forEach(Optimizer::generateFinalMessages);
        int newCount = program.stream().mapToInt(LogicInstruction::getRealSize).sum();
        messageRecipient.accept(MindcodeMessage.info("%6d instructions after optimizations.", newCount));

        if (modified) {
            messageRecipient.accept(MindcodeMessage.warn("\nOptimization passes limited at %d.",
                    profile.getOptimizationPasses()));
        }

        optimizationContext.removeInactiveLabels();
        optimizationStatistics.forEach(messageRecipient);

        return List.copyOf(program);
    }

    private boolean optimizePhase(OptimizationPhase phase, Map<Optimization, Optimizer> optimizers, int pass, GeneratorOutput generatorOutput) {
        boolean modified = false;
        for (Optimization optimization : phase.optimizations) {
            Optimizer optimizer = optimizers.get(optimization);
            if (optimizer != null) {
                if (optimizer.optimize(phase, pass)) {
                    modified = true;
                }
            }
        }

        if (phase != ITERATED) {
            return modified;
        }

        while (true) {
            int costLimit = profile.getGoal() == GenerationGoal.SIZE
                    ? 0
                    : Math.max(0, 1000 - program.stream().mapToInt(LogicInstruction::getRealSize).sum());

            optimizationContext.prepare();
            List<OptimizationAction> possibleOptimizations = phase.optimizations.stream()
                    .map(optimizers::get)
                    .filter(Objects::nonNull)
                    .flatMap(o -> o.getPossibleOptimizations(costLimit).stream())
                    .toList();
            optimizationContext.finish();

            if (possibleOptimizations.isEmpty()) {
                break;
            }

            OptimizationAction selectedAction = possibleOptimizations.stream().max(ACTION_COMPARATOR).get();
            optimizationStatistics.add(MindcodeMessage.debug(
                    "\nPass %d: speed optimization selection (cost limit %d):", pass, costLimit));
            possibleOptimizations.forEach(t -> outputPossibleOptimization(t, selectedAction));
            OptimizationResult result = selectedAction.apply(costLimit);
            modified |= result == OptimizationResult.REALIZED;
        }

        return modified;
    }

    private static final Comparator<OptimizationAction> ACTION_COMPARATOR =
            Comparator.comparingDouble(OptimizationAction::efficiency)
                    .thenComparing(Comparator.comparingInt(OptimizationAction::cost).reversed());

    private void outputPossibleOptimization(OptimizationAction opt, OptimizationAction selected) {
        optimizationStatistics.add(MindcodeMessage.debug(
                "  %s %-50s cost %3d, benefit %10.1f, efficiency %10.1f",
                opt == selected ? "*" : " ", opt, opt.cost(), opt.benefit(), opt.efficiency()));
    }
}
