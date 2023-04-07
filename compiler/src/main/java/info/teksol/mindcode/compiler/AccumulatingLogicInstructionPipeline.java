package info.teksol.mindcode.compiler;

import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import java.util.ArrayList;
import java.util.List;

public class AccumulatingLogicInstructionPipeline implements LogicInstructionPipeline {
    private final List<LogicInstruction> result = new ArrayList<>();

    public List<LogicInstruction> getResult() {
        return result;
    }

    @Override
    public void emit(LogicInstruction instruction) {
        result.add(instruction);
    }

    @Override
    public void flush() {
    }
}
