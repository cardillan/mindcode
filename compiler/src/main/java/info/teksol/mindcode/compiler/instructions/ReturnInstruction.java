package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class ReturnInstruction extends BaseInstruction {

    ReturnInstruction(String marker, Opcode opcode, List<String> args) {
        super(marker, opcode, args);
    }

    public final String getMemory() {
        return getArg(0);
    }
}
