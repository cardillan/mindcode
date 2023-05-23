package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class GotoInstruction extends BaseInstruction {

    GotoInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.GOTO, args, params);
    }

    protected GotoInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    @Override
    public GotoInstruction copy() {
        return new GotoInstruction(this, marker);
    }

    public GotoInstruction withMarker(String marker) {
        return new GotoInstruction(this, marker);
    }

    public final LogicVariable getIndirectAddress() {
        return (LogicVariable) getArg(0);
    }
}
