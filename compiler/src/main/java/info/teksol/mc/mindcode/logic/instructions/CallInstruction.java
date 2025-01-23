package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class CallInstruction extends BaseInstruction implements CallingInstruction {

    CallInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
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
