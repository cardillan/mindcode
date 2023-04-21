package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.*;

import java.util.List;

public class SetInstruction extends BaseInstruction {

    SetInstruction(List<LogicArgument> args, List<LogicParameter> params) {
        super(Opcode.SET, args, params);
    }

    protected SetInstruction(BaseInstruction other, String marker) {
        super(other, marker);
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
