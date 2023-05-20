package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.GenerationGoal;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;

public interface Optimizer extends LogicInstructionPipeline {

    String getName();

    void setLevel(OptimizationLevel level);

    void setGoal(GenerationGoal goal);

    void setDebugPrinter(DebugPrinter debugPrinter);
}
