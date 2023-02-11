package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.mindustry.instructions.LogicInstruction;

public interface LogicInstructionPipeline {
    void emit(LogicInstruction instruction);

    void flush();
}
