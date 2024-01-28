package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.*;

import java.util.List;

public class CallRecInstruction extends BaseInstruction implements CallingInstruction {

    CallRecInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.CALLREC, args, params);
    }

    protected CallRecInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public CallRecInstruction copy() {
        return new CallRecInstruction(this, astContext);
    }

    @Override
    public CallRecInstruction withContext(AstContext astContext) {
        return new CallRecInstruction(this, astContext);
    }

    public final LogicVariable getStack() {
        return (LogicVariable) getArg(0);
    }

    public final LogicLabel getCallAddr() {
        return (LogicLabel) getArg(1);
    }

    public final LogicLabel getRetAddr() {
        return (LogicLabel) getArg(2);
    }

    public final List<LogicLabel> getAddresses() {
        return List.of(getCallAddr(), getRetAddr());
    }
}
