package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class SetInstruction extends BaseInstruction implements LogicResultInstruction {

    SetInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params, String marker) {
        super(astContext, Opcode.SET, args, params, marker);
    }

    protected SetInstruction(BaseInstruction other, AstContext astContext, String marker) {
        super(other, astContext, marker);
    }

    @Override
    public SetInstruction copy() {
        return new SetInstruction(this, astContext, marker);
    }

    public SetInstruction withMarker(String marker) {
        return new SetInstruction(this, astContext, marker);
    }

    @Override
    public SetInstruction withContext(AstContext astContext) {
        return new SetInstruction(this, astContext, marker);
    }

    @Override
    public SetInstruction withResult(LogicVariable result) {
        return new SetInstruction(astContext, List.of(result, getValue()), getParams(), marker);
    }

    public SetInstruction withValue(LogicValue value) {
        return new SetInstruction(astContext, List.of(getResult(), value), getParams(), marker);
    }

    @Override
    public final LogicVariable getResult() {
        return (LogicVariable) getArg(0);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(1);
    }
}
