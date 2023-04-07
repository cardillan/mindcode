package info.teksol.mindcode.compiler;

import info.teksol.mindcode.compiler.instructions.LogicInstruction;

public interface LogicInstructionPipeline {
    void emit(LogicInstruction instruction);

    void flush();
}
