package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;

import java.util.List;

public class OpInstruction extends BaseResultInstruction {

    OpInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.OP, args, params);
    }

    protected OpInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public OpInstruction copy() {
        return new OpInstruction(this, astContext);
    }

    @Override
    public OpInstruction withContext(AstContext astContext) {
        return new OpInstruction(this, astContext);
    }

    @Override
    public OpInstruction withResult(LogicVariable result) {
        return hasSecondOperand()
                ? new OpInstruction(astContext, List.of(getArg(0), result, getArg(2), getArg(3)), getArgumentTypes())
                : new OpInstruction(astContext, List.of(getArg(0), result, getArg(2)), getArgumentTypes());
    }

    public OpInstruction withX(LogicValue x) {
        return hasSecondOperand()
                ? new OpInstruction(astContext, List.of(getArg(0), getArg(1), x, getArg(3)), getArgumentTypes())
                : new OpInstruction(astContext, List.of(getArg(0), getArg(1), x), getArgumentTypes());
    }

    public OpInstruction withY(LogicValue y) {
        return new OpInstruction(astContext, List.of(getArg(0), getArg(1), getArg(2), y), getArgumentTypes());
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

    public final LogicValue getX() {
        return (LogicValue) getArg(2);
    }

    public final LogicValue getY() {
        return (LogicValue) getArg(3);
    }

    public boolean isDeterministic() {
        return getOperation().isDeterministic();
    }
}
