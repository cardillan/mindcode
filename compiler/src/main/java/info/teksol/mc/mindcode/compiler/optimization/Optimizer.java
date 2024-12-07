package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.profile.GenerationGoal;
import info.teksol.mc.profile.MemoryModel;

import java.util.List;

public interface Optimizer {

    String getName();

    Optimization getOptimization();

    void setLevel(OptimizationLevel level);

    void setGoal(GenerationGoal goal);

    void setMemoryModel(MemoryModel memoryModel);

    void setMessageRecipient(MessageConsumer messageRecipient);

    void setDebugPrinter(DebugPrinter debugPrinter);

    /**
     * Performs general optimizations of the program. All optimizations that do not increase program size can be
     * performed here.
     *
     * @param phase current optimization phase
     * @param pass current optimization pass
     * @return true if the program was actually modified
     */
    boolean optimize(OptimizationPhase phase, int pass);

    /**
     * Creates a list of possible optimizations. It doesn't make sense to propose optimizations with cost higher
     * than the limit - there isn't enough remaining space to use.
     *
     * @return list of possible optimizations
     */
    List<OptimizationAction> getPossibleOptimizations(int costLimit);

    void generateFinalMessages();
}
