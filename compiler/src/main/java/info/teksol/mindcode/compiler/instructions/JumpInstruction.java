package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.*;

import java.util.List;

public class JumpInstruction extends BaseInstruction {

    JumpInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.JUMP, args, params);
    }

    protected JumpInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public JumpInstruction copy() {
        return new JumpInstruction(this, astContext);
    }

    @Override
    public JumpInstruction withContext(AstContext astContext) {
        return new JumpInstruction(this, astContext);
    }

    public JumpInstruction withTarget(LogicLabel target) {
        return isUnconditional()
                ? new JumpInstruction(getAstContext(), List.of(target, Condition.ALWAYS), getArgumentTypes())
                : new JumpInstruction(getAstContext(),List.of(target, getCondition(), getX(), getY()), getArgumentTypes());
    }

    public JumpInstruction invert() {
        if (!isInvertible()) {
            throw new MindcodeInternalError("Jump is not invertible. " + this);
        }
        return new JumpInstruction(getAstContext(),
                List.of(getTarget(), getCondition().inverse(), getX(), getY()), getArgumentTypes());
    }

    @Override
    public boolean endsCodePath() {
        return isUnconditional();
    }

    public boolean isConditional() {
        return getCondition() != Condition.ALWAYS;
    }

    public boolean isUnconditional() {
        return getCondition() == Condition.ALWAYS;
    }

    public boolean isInvertible() {
        return getCondition().hasInverse();
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
            throw new IllegalArgumentException("Conditional jump required, got " + this);
        }
    }
}
