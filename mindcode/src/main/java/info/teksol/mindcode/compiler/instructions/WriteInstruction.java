package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.*;

import java.util.List;

public class WriteInstruction extends BaseInstruction {

    WriteInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.WRITE, args, params);
    }

    protected WriteInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public WriteInstruction copy() {
        return new WriteInstruction(this, astContext);
    }

    @Override
    public WriteInstruction withContext(AstContext astContext) {
        return new WriteInstruction(this, astContext);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(0);
    }

    public final LogicAddress getAddress() {
        return (LogicAddress) getArg(0);
    }

    public final LogicVariable getMemory() {
        return (LogicVariable) getArg(1);
    }

    public final LogicValue getIndex() {
        return (LogicValue) getArg(2);
    }
}
