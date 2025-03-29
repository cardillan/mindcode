package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class ReturnInstruction extends BaseInstruction {

    ReturnInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.RETURN, args, params);
    }

    protected ReturnInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public ReturnInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new ReturnInstruction(this, astContext);
    }

    @Override
    public boolean affectsControlFlow() {
        return true;
    }

    public final LogicVariable getIndirectAddress() {
        return (LogicVariable) getArg(0);
    }

    @Override
    public boolean endsCodePath() {
        return true;
    }
}
