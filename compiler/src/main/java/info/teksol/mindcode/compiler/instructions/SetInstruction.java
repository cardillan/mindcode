package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class SetInstruction extends BaseInstruction {

    SetInstruction(String marker, Opcode opcode, List<String> args) {
        super(marker, opcode, args);
    }

    public final String getResult() {
        return getArg(0);
    }

    public final String getValue() {
        return getArg(1);
    }
}
