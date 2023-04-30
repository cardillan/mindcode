package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.*;

import java.util.List;

public class SetAddressInstruction extends BaseInstruction {

    SetAddressInstruction(List<LogicArgument> args, List<LogicParameter> params) {
        super(Opcode.SETADDR, args, params);
    }

    protected SetAddressInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    public SetAddressInstruction withMarker(String marker) {
        return new SetAddressInstruction(this, marker);
    }

    public final LogicVariable getTarget() {
        return (LogicVariable) getArg(0);
    }

    public final LogicLabel getLabel() {
        return (LogicLabel) getArg(1);
    }
}
