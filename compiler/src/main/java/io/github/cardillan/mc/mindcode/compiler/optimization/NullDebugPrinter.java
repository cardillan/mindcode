package io.github.cardillan.mc.mindcode.compiler.optimization;

import io.github.cardillan.mc.mindcode.logic.instructions.LogicInstruction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

@NullMarked
public class NullDebugPrinter implements DebugPrinter {
    @Override
    public void registerIteration(@Nullable Optimizer optimizer, String title, List<LogicInstruction> program) {
        // Do nothing
    }

    @Override
    public void print(Consumer<String> messageConsumer) {
        // Do nothing
    }
}
