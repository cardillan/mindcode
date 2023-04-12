package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class PrintflushInstruction extends BaseInstruction {

    PrintflushInstruction(String marker, Opcode opcode, List<String> args) {
        super(marker, opcode, args);
    }

    public final String getBlock() {
        return getArg(0);
    }
}
