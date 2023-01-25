package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.LogicInstruction;
import java.util.List;
import java.util.function.Consumer;

public class NullDebugPrinter implements DebugPrinter {
    @Override
    public void instructionEmmited(Optimizer optimizer, LogicInstruction instruction) {
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
