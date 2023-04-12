package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class PrintInstruction extends BaseInstruction {

    PrintInstruction(String marker, Opcode opcode, List<String> args) {
        super(marker, opcode, args);
    }

    public final String getValue() {
        return getArg(0);
    }
}
