package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.GenerationException;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;
import info.teksol.mindcode.logic.Operation;

import java.util.List;

public class OpInstruction extends BaseInstruction implements LogicResultInstruction {

    OpInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.OP, args, params);
    }

    protected OpInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    @Override
    public OpInstruction copy() {
        return new OpInstruction(this, marker);
    }

    public OpInstruction withMarker(String marker) {
        return new OpInstruction(this, marker);
    }

    @Override
    public OpInstruction withResult(LogicVariable result) {
        return hasSecondOperand()
                ? new OpInstruction(getAstContext(), List.of(getArg(0), result, getArg(2), getArg(3)), getParams()).withMarker(marker)
                : new OpInstruction(getAstContext(), List.of(getArg(0), result, getArg(2)), getParams()).withMarker(marker);
    }

    public boolean hasSecondOperand() {
        return getArgs().size() > 3;
    }

    public final Operation getOperation() {
        if (getArg(0) instanceof Operation op) {
            return op;
        } else {
            throw new GenerationException(getArg(0) + " is not an operation.");
        }
    }

    @Override
    public final LogicVariable getResult() {
        return (LogicVariable) getArg(1);
    }

    public final LogicValue getX() {
        return (LogicValue) getArg(2);
    }

    public final LogicValue getY() {
        return (LogicValue) getArg(3);
    }

    public final LogicValue getOperand(int index) {
        if (index < 0 || index > getArgs().size() - 2) {
            throw new ArrayIndexOutOfBoundsException("Operand index " + index + " out of bounds");
        }
        return (LogicValue) getArg(index + 2);
    }

    public final List<LogicValue> getOperands() {
        return List.of(getX(), getY());
    }
}
