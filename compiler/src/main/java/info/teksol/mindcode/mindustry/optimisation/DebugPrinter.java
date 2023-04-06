package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import java.util.List;
import java.util.function.Consumer;

/**
 * Collects instructions produced by optimizers from the entire pipeline, for later analysis of effects of
 * optimizations made by individual optimizers.
 */
public interface DebugPrinter {

    /**
     * Called by pipelined optimizers whenever an instruction is emitted.
     *
     * @param optimizer instance of the optimizer
     * @param instruction the emitted instruction
     */
    void instructionEmitted(Optimizer optimizer, LogicInstruction instruction);

    /**
     * Called by global optimizers at the end of each iteration performed.
     *
     * @param optimizer instance of the optimizer
     * @param iteration number of the performed iteration
     * @param program state of the program after the iteration was performed
     */
    void iterationFinished(Optimizer optimizer, int iteration, List<LogicInstruction> program);

    /**
     * Processes and outputs collected information for later analysis.
     *
     * @param messageConsumer recipient of the messages produced
     */
    void print(Consumer<String> messageConsumer);
}
