package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.Opcode;

import java.util.List;

// Push and pop are always processed at the same time
public abstract class PushOrPopInstruction extends BaseInstruction {

    PushOrPopInstruction(String marker, Opcode opcode, List<String> args) {
        super(marker, opcode, args);
    }

    public final String getMemory() {
        return getArg(0);
    }

    public final String getValue() {
        return getArg(1);
    }
}
