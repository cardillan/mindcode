package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class JumpInstruction extends BaseInstruction {

    JumpInstruction(String marker, Opcode opcode, List<String> args) {
        super(marker, opcode, args);
    }

    public boolean isUnconditional() {
        return "always".equals(getCondition());
    }

    public final String getTarget() {
        return getArg(0);
    }

    public final String getCondition() {
        return getArg(1);
    }

    public final String getFirstOperand() {
        ensureConditional();
        return getArg(2);
    }

    public final String getSecondOperand() {
        ensureConditional();
        return getArg(3);
    }

    public final String getOperand(int index) {
        ensureConditional();
        if (index < 0 || index > 1) {
            throw new ArrayIndexOutOfBoundsException("Operand index must be between 0 and 1, got " + index);
        }
        return getArg(index + 2);
    }

    public final List<String> getOperands() {
        ensureConditional();
        return getArgs().subList(2, 4);
    }

    private void ensureConditional() {
        if (isUnconditional()) {
            throw new IllegalArgumentException("Conditional instruction required, got " + this);
        }
    }
}
