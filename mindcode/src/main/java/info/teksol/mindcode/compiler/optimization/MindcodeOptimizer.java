package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.MindcodeMessage;
import info.teksol.mindcode.compiler.generator.GeneratorOutput;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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

    protected List<Optimizer> getOptimizers() {
        List<Optimizer> result = new ArrayList<>();
        for (Optimization optimization : Optimization.LIST) {
            OptimizationLevel level = profile.getOptimizationLevel(optimization);
            if (level != OptimizationLevel.OFF) {
                for (Function<InstructionProcessor, Optimizer> ic : optimization.getInstanceCreators()) {
                    Optimizer optimizer = ic.apply(instructionProcessor);
                    optimizer.setLevel(level);
                    result.add(optimizer);
                }
            }
        }

        return result;
    }

    public List<LogicInstruction> optimize(GeneratorOutput generatorOutput) {
        program.addAll(generatorOutput.instructions());

        debugPrinter.registerIteration(null, 0, List.copyOf(program));

        for (Optimizer optimizer : getOptimizers()) {
            optimizer.setGoal(profile.getGoal());
            optimizer.setMessageRecipient(messageRecipient);
            optimizer.setDebugPrinter(debugPrinter);
            optimizer.optimizeProgram(program, generatorOutput.rootAstContext());
        }

        int count = program.stream().mapToInt(LogicInstruction::getRealSize).sum();
        messageRecipient.accept(MindcodeMessage.info("%6d instructions after optimizations.", count));
        return List.copyOf(program);
    }
}
