package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.LogicInstruction;

import java.util.List;
import java.util.function.Consumer;

/**
 * Collects instructions produced by optimizers from the entire pipeline, for later analysis of effects of
 * optimizations made by individual optimizers.
 */
public interface DebugPrinter {

    /**
     * Called by global optimizers at the end of each iteration performed.
     *
     * @param optimizer instance of the optimizer (null for unoptimized code)
     * @param iteration number of the performed iteration
     * @param program state of the program after the iteration was performed
     */
    void registerIteration(Optimizer optimizer, int iteration, List<LogicInstruction> program);

    /**
     * Processes and outputs collected information for later analysis.
     *
     * @param messageConsumer recipient of the messages produced
     */
    void print(Consumer<String> messageConsumer);
}
