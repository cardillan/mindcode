package info.teksol.mindcode.mindustry;

public interface LogicInstructionPipeline {
    void emit(LogicInstruction instruction);

    void flush();
}
