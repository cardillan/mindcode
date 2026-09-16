package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.GlobalCompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

@NullMarked
public class JumpInstruction extends BaseInstruction implements ConditionalInstruction {

    JumpInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.JUMP, args, params);
    }

    protected JumpInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public JumpInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new JumpInstruction(this, astContext);
    }

    @Override
    public boolean affectsControlFlow() {
        return true;
    }

    @Override
    protected void validate() {
        super.validate();
    }

    @Override
    public JumpInstruction withOperands(Condition condition, LogicValue x, LogicValue y) {
        assert getArgumentTypes() != null;
        ensureConditional();
        return new JumpInstruction(getAstContext(),List.of(getTarget(), condition, x, y), getArgumentTypes()).copyInfo(this);
    }

    public JumpInstruction withTarget(LogicLabel target) {
        assert getArgumentTypes() != null;
        return isUnconditional()
                ? new JumpInstruction(getAstContext(), List.of(target, Condition.ALWAYS), getArgumentTypes()).copyInfo(this)
                : new JumpInstruction(getAstContext(),List.of(target, getCondition(), getX(), getY()), getArgumentTypes()).copyInfo(this);
    }

    public JumpInstruction invert(GlobalCompilerProfile profile) {
        return (JumpInstruction) ConditionalInstruction.super.invert(profile);
    }

    public JumpInstruction forceInvert() {
        return (JumpInstruction) ConditionalInstruction.super.forceInvert();
    }

    @Override
    public boolean endsCodePath() {
        return isUnconditional();
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

    @Override
    public void resolve(InstructionProcessor processor, Consumer<LogicInstruction> consumer) {
        if (getCondition() == Condition.STRICT_NOT_EQUAL) {
            creator(processor, consumer).createSelect(LogicBuiltIn.COUNTER, Condition.STRICT_EQUAL,
                    getX(), getY(), LogicBuiltIn.COUNTER, getTarget()).copyComment(this);
        } else {
            consumer.accept(this);
        }
    }
}
