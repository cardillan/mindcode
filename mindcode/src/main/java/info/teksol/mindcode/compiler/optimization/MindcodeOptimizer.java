package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.MindcodeMessage;
import info.teksol.mindcode.compiler.generator.GeneratorOutput;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static info.teksol.mindcode.compiler.optimization.OptimizationPhase.*;

public class MindcodeOptimizer {
    private final List<LogicInstruction> program = new ArrayList<>();
    private final InstructionProcessor instructionProcessor;
    private final Consumer<CompilerMessage> messageRecipient;
    private final CompilerProfile profile;
    private DebugPrinter debugPrinter = new NullDebugPrinter();

    public MindcodeOptimizer(InstructionProcessor instructionProcessor, CompilerProfile profile,
            Consumer<CompilerMessage> messageRecipient) {
        this.instructionProcessor = instructionProcessor;
        this.messageRecipient = messageRecipient;
        this.profile = profile;
    }

    public void setDebugPrinter(DebugPrinter debugPrinter) {
        this.debugPrinter = debugPrinter;
    }

    protected Map<Optimization, Optimizer> getOptimizers() {
        Map<Optimization, Optimizer> result = new LinkedHashMap<>();
        for (Optimization optimization : Optimization.LIST) {
            OptimizationLevel level = profile.getOptimizationLevel(optimization);
            if (level != OptimizationLevel.OFF) {
                Function<InstructionProcessor, Optimizer> ic = optimization.getInstanceCreator();
                Optimizer optimizer = ic.apply(instructionProcessor);
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

    public List<LogicInstruction> optimize(GeneratorOutput generatorOutput) {
        program.addAll(generatorOutput.instructions());

        int count = program.stream().mapToInt(LogicInstruction::getRealSize).sum();
        messageRecipient.accept(MindcodeMessage.info("%6d instructions before optimizations.", count));

        debugPrinter.registerIteration(null, 0, 0, List.copyOf(program));

        Map<Optimization, Optimizer> optimizers = getOptimizers();

        int pass = 1;
        optimizePhase(INITIAL, optimizers, pass++,generatorOutput);
        while (optimizePhase(ITERATED, optimizers, pass, generatorOutput)) {
            pass++;
        }
        optimizePhase(FINAL, optimizers, pass, generatorOutput);

        optimizers.values().forEach(Optimizer::generateFinalMessages);

        int newCount = program.stream().mapToInt(LogicInstruction::getRealSize).sum();
        messageRecipient.accept(MindcodeMessage.info("%6d instructions after optimizations.", newCount));
        return List.copyOf(program);
    }

    private boolean optimizePhase(OptimizationPhase phase, Map<Optimization, Optimizer> optimizers, int pass, GeneratorOutput generatorOutput) {
        boolean modified = false;
        for (Optimization optimization : phase.optimizations) {
            Optimizer optimizer = optimizers.get(optimization);
            if (optimizer != null) {
                if (optimizer.optimizeProgram(phase, pass, program, generatorOutput.callGraph(),
                        generatorOutput.rootAstContext())) {
                    modified = true;
                }
            }
        }

        return modified;
    }
}
