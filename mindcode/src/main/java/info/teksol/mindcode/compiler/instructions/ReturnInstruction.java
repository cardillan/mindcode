package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class ReturnInstruction extends BaseInstruction {

    ReturnInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.RETURN, args, params);
    }

    protected ReturnInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public ReturnInstruction copy() {
        return new ReturnInstruction(this, astContext);
    }

    @Override
    public ReturnInstruction withContext(AstContext astContext) {
        return new ReturnInstruction(this, astContext);
    }

    public final LogicVariable getStack() {
        return (LogicVariable) getArg(0);
    }
}
