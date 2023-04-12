package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class GotoInstruction extends BaseInstruction {

    GotoInstruction(String marker, Opcode opcode, List<String> args) {
        super(marker, opcode, args);
    }

    public final String getLabel() {
        return getArg(0);
    }
}
