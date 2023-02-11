package info.teksol.mindcode.mindustry.instructions;

import info.teksol.mindcode.mindustry.logic.Opcode;
import java.util.List;

public class BaseInstructionProcessor implements InstructionProcessor {

    @Override
    public LogicInstruction createInstruction(Opcode opcode, String... arguments) {
        return new LogicInstruction(opcode, arguments);
    }

    @Override
    public LogicInstruction createInstruction(Opcode opcode, List<String> arguments) {
        return new LogicInstruction(opcode, arguments);
    }
}
