package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;

import java.util.List;

public class GotoOffsetInstruction extends BaseInstruction {

    GotoOffsetInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.GOTOOFFSET, args, params);
    }

    protected GotoOffsetInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public boolean affectsControlFlow() {
        return true;
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

    @Override
    public boolean endsCodePath() {
        return true;
    }
}
