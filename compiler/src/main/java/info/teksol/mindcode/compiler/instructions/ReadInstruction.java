package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class ReadInstruction extends BaseInstruction {

    ReadInstruction(String marker, Opcode opcode, List<String> args) {
        super(marker, opcode, args);
    }

    public final String getValue() {
        return getArg(0);
    }

    public final String getMemory() {
        return getArg(1);
    }

    public final String getIndex() {
        return getArg(2);
    }
}
