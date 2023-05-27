package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class ReturnInstruction extends BaseInstruction {

    ReturnInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params, String marker) {
        super(astContext, Opcode.RETURN, args, params, marker);
    }

    protected ReturnInstruction(BaseInstruction other, AstContext astContext, String marker) {
        super(other, astContext, marker);
    }

    @Override
    public ReturnInstruction copy() {
        return new ReturnInstruction(this, astContext, marker);
    }

    public ReturnInstruction withMarker(String marker) {
        return new ReturnInstruction(this, astContext, marker);
    }

    @Override
    public ReturnInstruction withContext(AstContext astContext) {
        return new ReturnInstruction(this, astContext, marker);
    }

    public final LogicVariable getStack() {
        return (LogicVariable) getArg(0);
    }
}
