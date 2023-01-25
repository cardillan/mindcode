package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.LogicInstructionPipeline;

public interface Optimizer extends LogicInstructionPipeline{

    String getName();

    void setDebugPrinter(DebugPrinter debugPrinter);

}
