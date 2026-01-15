package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.Condition;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class SelectInstruction extends BaseResultInstruction implements ConditionalInstruction {

    SelectInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.SELECT, args, params);
    }

    protected SelectInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public SelectInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new SelectInstruction(this, astContext);
    }

    @Override
    public SelectInstruction withResult(LogicVariable result) {
        assert getArgumentTypes() != null;
        return new SelectInstruction(astContext, List.of(result, getCondition(), getX(), getY(), getTrueValue(), getFalseValue()), getArgumentTypes()).copyInfo(this);
    }

    @Override
    public SelectInstruction withOperands(Condition condition, LogicValue x, LogicValue y) {
        assert getArgumentTypes() != null;
        ensureConditional();
        return new SelectInstruction(astContext, List.of(getResultArgument(), condition, x, y, getTrueValue(), getFalseValue()), getArgumentTypes()).copyInfo(this);
    }

    @Override
    public final Condition getCondition() {
        return (Condition) getArg(1);
    }

    @Override
    public final LogicValue getX() {
        ensureConditional();
        return (LogicValue) getArg(2);
    }

    @Override
    public final LogicValue getY() {
        ensureConditional();
        return (LogicValue) getArg(3);
    }

    public final LogicValue getTrueValue() {
        return (LogicValue) getArg(4);
    }

    public final LogicValue getFalseValue() {
        if (getArg(5) instanceof LogicValue value) return value;
        throw new MindcodeInternalError("Unexpected argument type " + getArg(5));
    }

    private void ensureConditional() {
        if (isUnconditional()) {
            throw new IllegalArgumentException("Conditional jump required, got " + this);
        }
    }

    public LogicValue getResultValue() {
        return (LogicValue) getArg(resultIndex);
    }
}
