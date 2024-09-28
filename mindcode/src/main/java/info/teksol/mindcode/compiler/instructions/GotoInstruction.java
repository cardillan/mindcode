package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.*;

import java.util.List;

public class GotoInstruction extends BaseInstruction {

    GotoInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.GOTO, args, params);
    }

    protected GotoInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
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
