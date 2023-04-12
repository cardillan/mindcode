package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class CallInstruction extends BaseInstruction {

    CallInstruction(String marker, Opcode opcode, List<String> args) {
        super(marker, opcode, args);
    }

    public final String getMemory() {
        return getArg(0);
    }

    public final String getTarget() {
        return getArg(1);
    }

    public final String getReturn() {
        return getArg(2);
    }

    public final List<String> getAddresses() {
        return getArgs().subList(1, 3);
    }
}
