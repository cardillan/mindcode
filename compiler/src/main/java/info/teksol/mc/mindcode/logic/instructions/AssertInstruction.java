package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.Condition;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.GlobalCompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class AssertInstruction extends BaseInstruction implements ConditionalInstruction {

    AssertInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.ASSERT, args, params);
    }

    protected AssertInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public AssertInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new AssertInstruction(this, astContext);
    }

    @Override
    public AssertInstruction withOperands(Condition condition, LogicValue x, LogicValue y) {
        assert getArgumentTypes() != null;
        ensureConditional();
        return new AssertInstruction(getAstContext(),List.of(condition, x, y, getMessage()), getArgumentTypes()).copyInfo(this);
    }

    public boolean isInvertible(GlobalCompilerProfile profile) {
        return getCondition().hasInverse(false);
    }

    public AssertInstruction invert(GlobalCompilerProfile profile) {
        return (AssertInstruction) ConditionalInstruction.super.invert(profile);
    }

    public final Condition getCondition() {
        return (Condition) getArg(0);
    }

    public final LogicValue getX() {
        ensureConditional();
        return (LogicValue) getArg(1);
    }

    public final LogicValue getY() {
        ensureConditional();
        return (LogicValue) getArg(2);
    }

    public final LogicValue getMessage() {
        ensureConditional();
        return (LogicValue) getArg(3);
    }

    public final LogicValue getOperand(int index) {
        ensureConditional();
        if (index < 0 || index > 1) {
            throw new ArrayIndexOutOfBoundsException("Operand index must be between 0 and 1, got " + index);
        }
        return (LogicValue) getArg(index + 1);
    }

    public final List<LogicValue> getOperands() {
        ensureConditional();
        return List.of(getX(), getY());
    }

    private void ensureConditional() {
        if (isUnconditional()) {
            throw new IllegalArgumentException("Conditional assert required, got " + this);
        }
    }
}
