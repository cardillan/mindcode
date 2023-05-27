package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class GotoInstruction extends BaseInstruction {

    GotoInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params, String marker) {
        super(astContext, Opcode.GOTO, args, params, marker);
    }

    protected GotoInstruction(BaseInstruction other, AstContext astContext, String marker) {
        super(other, astContext, marker);
    }

    @Override
    public GotoInstruction copy() {
        return new GotoInstruction(this, astContext, marker);
    }

    public GotoInstruction withMarker(String marker) {
        return new GotoInstruction(this, astContext, marker);
    }

    @Override
    public GotoInstruction withContext(AstContext astContext) {
        return new GotoInstruction(this, astContext, marker);
    }

    public final LogicVariable getIndirectAddress() {
        return (LogicVariable) getArg(0);
    }
}
