package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;

import java.util.List;

public class GotoInstruction extends BaseInstruction {

    GotoInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.GOTO, args, params);
    }

    protected GotoInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public boolean affectsControlFlow() {
        return true;
    }

    @Override
    public GotoInstruction copy() {
        return new GotoInstruction(this, astContext);
    }

    @Override
    public GotoInstruction withContext(AstContext astContext) {
        return new GotoInstruction(this, astContext);
    }

    public final LogicVariable getIndirectAddress() {
        return (LogicVariable) getArg(0);
    }

    public final LogicLabel getMarker() {
        return (LogicLabel) getArg(1);
    }

    @Override
    public boolean endsCodePath() {
        return true;
    }
}
