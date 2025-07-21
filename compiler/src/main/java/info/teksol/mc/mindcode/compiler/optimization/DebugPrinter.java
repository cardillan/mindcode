package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/// Collects instructions produced by optimizers from the entire pipeline for later analysis of the
/// optimizations made by individual optimizers.
@NullMarked
public interface DebugPrinter {

    /// Called by global optimizers at the end of each iteration performed.
    ///
    /// @param optimizer instance of the optimizer (null for unoptimized code)
    /// @param title title for the iteration being registered
    /// @param program state of the program after the iteration was performed
    void registerIteration(@Nullable Optimizer optimizer, String title, List<LogicInstruction> program);

    /// Processes and outputs collected information for later analysis.
    ///
    /// @param messageConsumer recipient of the messages produced
    void print(Consumer<String> messageConsumer);
}
