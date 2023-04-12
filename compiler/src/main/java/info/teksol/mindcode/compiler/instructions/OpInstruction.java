package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class OpInstruction extends BaseInstruction {

    OpInstruction(String marker, Opcode opcode, List<String> args) {
        super(marker, opcode, args);
    }

    public boolean hasSecondOperand() {
        return getArgs().size() > 3;
    }

    public final  String getOperation() {
        return getArg(0);
    }

    public final String getResult() {
        return getArg(1);
    }

    public final String getFirstOperand() {
        return getArg(2);
    }

    public final String getSecondOperand() {
        return getArg(3);
    }

    public final String getOperand(int index) {
        if (index < 0 || index > getArgs().size() - 2) {
            throw new ArrayIndexOutOfBoundsException("Operand index " + index + " out of bounds");
        }
        return getArg(index + 2);
    }

    public final List<String> getOperands() {
        return getArgs().subList(2, getArgs().size());
    }
}
