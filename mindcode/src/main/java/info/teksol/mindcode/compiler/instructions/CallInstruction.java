package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.*;

import java.util.List;

public class CallInstruction extends BaseInstruction implements CallingInstruction {

    CallInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.CALL, args, params);
    }

    protected CallInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public CallInstruction copy() {
        return new CallInstruction(this, astContext);
    }

    @Override
    public CallInstruction withContext(AstContext astContext) {
        return new CallInstruction(this, astContext);
    }

    public final LogicLabel getCallAddr() {
        return (LogicLabel) getArg(0);
    }

    public final LogicVariable getReturnValue()  {
        return (LogicVariable) getArg(1);
    }
}
