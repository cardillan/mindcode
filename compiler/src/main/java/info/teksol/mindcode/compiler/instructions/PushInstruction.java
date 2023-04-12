package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class PushInstruction extends PushOrPopInstruction {

    public PushInstruction(String marker, Opcode opcode, List<String> args) {
        super(marker, opcode, args);
    }
}
