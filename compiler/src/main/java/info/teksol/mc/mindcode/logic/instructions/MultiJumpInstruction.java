package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;

import java.util.List;

public class MultiJumpInstruction extends BaseInstruction {

    MultiJumpInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.MULTIJUMP, args, params);
    }

    protected MultiJumpInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public boolean affectsControlFlow() {
        return true;
    }

    @Override
    public MultiJumpInstruction copy() {
        return new MultiJumpInstruction(this, astContext);
    }

    @Override
    public MultiJumpInstruction withContext(AstContext astContext) {
        return new MultiJumpInstruction(this, astContext);
    }

    public final LogicAddress getTarget() {
        return (LogicAddress) getArg(0);
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
