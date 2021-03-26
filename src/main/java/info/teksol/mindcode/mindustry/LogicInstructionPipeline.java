package info.teksol.mindcode.mindustry;

import java.util.List;

public interface LogicInstructionPipeline {
    void emit(LogicInstruction instruction);

    void flush();
}
