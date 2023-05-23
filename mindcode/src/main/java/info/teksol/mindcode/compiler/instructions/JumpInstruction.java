package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.Condition;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class JumpInstruction extends BaseInstruction {

    JumpInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.JUMP, args, params);
    }

    @Override
    public JumpInstruction copy() {
        return new JumpInstruction(this, marker);
    }
    protected JumpInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }


    public JumpInstruction withMarker(String marker) {
        return new JumpInstruction(this, marker);
    }

    public JumpInstruction withLabel(LogicLabel label) {
        return isUnconditional()
                ? new JumpInstruction(getAstContext(), List.of(label, Condition.ALWAYS), getParams())
                : new JumpInstruction(getAstContext(),List.of(label, getCondition(), getX(), getY()), getParams());
    }

    public boolean isUnconditional() {
        return getCondition() == Condition.ALWAYS;
    }

    public final LogicLabel getTarget() {
        return (LogicLabel) getArg(0);
    }

    public final Condition getCondition() {
        return (Condition) getArg(1);
    }

    public final LogicValue getX() {
        ensureConditional();
        return (LogicValue) getArg(2);
    }

    public final LogicValue getY() {
        ensureConditional();
        return (LogicValue) getArg(3);
    }

    public final LogicValue getOperand(int index) {
        ensureConditional();
        if (index < 0 || index > 1) {
            throw new ArrayIndexOutOfBoundsException("Operand index must be between 0 and 1, got " + index);
        }
        return (LogicValue) getArg(index + 2);
    }

    public final List<LogicValue> getOperands() {
        ensureConditional();
        return List.of(getX(), getY());
    }

    private void ensureConditional() {
        if (isUnconditional()) {
            throw new IllegalArgumentException("Conditional instruction required, got " + this);
        }
    }
}
