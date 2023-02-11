package info.teksol.mindcode.mindustry.instructions;

import info.teksol.mindcode.mindustry.logic.Opcode;
import java.util.List;

public interface InstructionProcessor {

    LogicInstruction createInstruction(Opcode opcode, String... arguments);

    LogicInstruction createInstruction(Opcode opcode, List<String> arguments);

}
