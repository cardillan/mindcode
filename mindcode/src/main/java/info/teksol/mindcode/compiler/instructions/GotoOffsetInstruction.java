package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.*;

import java.util.List;

public class GotoOffsetInstruction extends BaseInstruction {

    GotoOffsetInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.GOTOOFFSET, args, params);
    }

    protected GotoOffsetInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public GotoOffsetInstruction copy() {
        return new GotoOffsetInstruction(this, astContext);
    }

    @Override
    public GotoOffsetInstruction withContext(AstContext astContext) {
        return new GotoOffsetInstruction(this, astContext);
    }

    public final LogicLabel getTarget() {
        return (LogicLabel) getArg(0);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(1);
    }

    public final LogicNumber getOffset() {
        return (LogicNumber) getArg(2);
    }

    public final LogicLabel getMarker() {
        return (LogicLabel) getArg(3);
    }
}
