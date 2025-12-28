package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@NullMarked
public class OpInstruction extends BaseResultInstruction implements ConditionalInstruction {

    OpInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.OP, args, params);
    }

    protected OpInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public OpInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new OpInstruction(this, astContext);
    }

    @Override
    public OpInstruction withOperands(Condition condition, LogicValue x, LogicValue y) {
        assert getArgumentTypes() != null;
        ensureConditional();
        return new OpInstruction(astContext,List.of(condition.toOperation(), getResultArgument(), x, y), getArgumentTypes()).copyInfo(this);
    }

    public OpInstruction withOperands(Operation operation, LogicValue x, LogicValue y) {
        assert getArgumentTypes() != null;
        return new OpInstruction(astContext,List.of(operation, getResultArgument(), x, y), getArgumentTypes()).copyInfo(this);
    }

    @Override
    public OpInstruction withResult(LogicVariable result) {
        assert getArgumentTypes() != null;
        return hasSecondOperand()
                ? new OpInstruction(astContext, List.of(getArg(0), result, getArg(2), getArg(3)), getArgumentTypes()).copyInfo(this)
                : new OpInstruction(astContext, List.of(getArg(0), result, getArg(2)), getArgumentTypes()).copyInfo(this);
    }

    public OpInstruction withOperationAndResult(Operation operation, LogicVariable result) {
        assert getArgumentTypes() != null;
        return hasSecondOperand()
                ? new OpInstruction(astContext, List.of(operation, result, getArg(2), getArg(3)), getArgumentTypes()).copyInfo(this)
                : new OpInstruction(astContext, List.of(operation, result, getArg(2)), getArgumentTypes()).copyInfo(this);
    }

    public OpInstruction withX(LogicValue x) {
        assert getArgumentTypes() != null;
        return hasSecondOperand()
                ? new OpInstruction(astContext, List.of(getArg(0), getArg(1), x, getArg(3)), getArgumentTypes()).copyInfo(this)
                : new OpInstruction(astContext, List.of(getArg(0), getArg(1), x), getArgumentTypes()).copyInfo(this);
    }

    public OpInstruction withY(LogicValue y) {
        assert getArgumentTypes() != null;
        return new OpInstruction(astContext, List.of(getArg(0), getArg(1), getArg(2), y), getArgumentTypes()).copyInfo(this);
    }

    public boolean hasSecondOperand() {
        return getArgs().size() > 3;
    }

    public final Operation getOperation() {
        if (getArg(0) instanceof Operation op) {
            return op;
        } else {
            throw new MindcodeInternalError(getArg(0) + " is not an operation.");
        }
    }

    public final Condition getCondition() {
        return switch (getOperation()) {
            case EQUAL -> Condition.EQUAL;
            case NOT_EQUAL -> Condition.NOT_EQUAL;
            case LESS_THAN -> Condition.LESS_THAN;
            case LESS_THAN_EQ -> Condition.LESS_THAN_EQ;
            case GREATER_THAN -> Condition.GREATER_THAN;
            case GREATER_THAN_EQ -> Condition.GREATER_THAN_EQ;
            case STRICT_EQUAL -> Condition.STRICT_EQUAL;
            case STRICT_NOT_EQUAL -> Condition.STRICT_NOT_EQUAL;
            default -> Condition.ALWAYS;
        };
    }


    public final LogicValue getX() {
        return (LogicValue) getArg(2);
    }

    public final LogicValue getY() {
        return (LogicValue) getArg(3);
    }

    private void ensureConditional() {
        if (isUnconditional()) {
            throw new IllegalArgumentException("Conditional op required, got " + this);
        }
    }

    @Override
    public int getSharedSize(@Nullable Map<String, Integer> sharedStructures) {
        return getArg(0) == Operation.BOOLEAN_OR ? 2 : 1;
    }
}
