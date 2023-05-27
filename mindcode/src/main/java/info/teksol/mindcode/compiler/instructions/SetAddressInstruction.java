package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class SetAddressInstruction extends BaseInstruction {

    SetAddressInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.SETADDR, args, params);
    }

    protected SetAddressInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    @Override
    public SetAddressInstruction copy() {
        return new SetAddressInstruction(this, marker);
    }

    public SetAddressInstruction withMarker(String marker) {
        return new SetAddressInstruction(this, marker);
    }

    public final LogicVariable getResult() {
        return (LogicVariable) getArg(0);
    }

    public final LogicLabel getLabel() {
        return (LogicLabel) getArg(1);
    }
}
