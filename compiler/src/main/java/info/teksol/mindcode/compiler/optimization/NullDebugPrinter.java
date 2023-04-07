package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import java.util.List;
import java.util.function.Consumer;

public class NullDebugPrinter implements DebugPrinter {
    @Override
    public void instructionEmitted(Optimizer optimizer, LogicInstruction instruction) {
        // Do nothing
    }

    @Override
    public void iterationFinished(Optimizer optimizer, int iteration, List<LogicInstruction> program) {
        // Do nothing
    }

    @Override
    public void print(Consumer<String> messageConsumer) {
        // Do nothing
    }
}
