package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class SetInstruction extends BaseInstruction {

    SetInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.SET, args, params);
    }

    protected SetInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    @Override
    public SetInstruction copy() {
        return new SetInstruction(this, marker);
    }

    public SetInstruction withMarker(String marker) {
        return new SetInstruction(this, marker);
    }

    public final LogicVariable getTarget() {
        return (LogicVariable) getArg(0);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(1);
    }
}
