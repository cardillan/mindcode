package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class PackColorInstruction extends BaseInstruction {
    public PackColorInstruction(String marker, Opcode opcode, List<String> args) {
        super(marker, opcode, args);
    }

    public final String getResult() {
        return getArg(0);
    }

    public final String getR() {
        return getArg(1);
    }

    public final String getG() {
        return getArg(2);
    }

    public final String getB() {
        return getArg(3);
    }

    public final String getA() {
        return getArg(4);
    }
}
