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
import java.util.Map;

@NullMarked
public class CallRecInstruction extends BaseInstruction implements CallingInstruction {

    CallRecInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.CALLREC, args, params);
    }

    protected CallRecInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public CallRecInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new CallRecInstruction(this, astContext);
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

    public int getRealSize(@Nullable Map<String, Integer> sharedStructures) {
        return super.getRealSize(sharedStructures) + (astContext.getProfile().isSymbolicLabels() ? 1 : 0);
    }
}
