package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.*;

import java.util.List;

public class ReadInstruction extends BaseResultInstruction {

    ReadInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.READ, args, params);
    }

    protected ReadInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public ReadInstruction copy() {
        return new ReadInstruction(this, astContext);
    }

    @Override
    public ReadInstruction withContext(AstContext astContext) {
        return new ReadInstruction(this, astContext);
    }

    @Override
    public ReadInstruction withResult(LogicVariable result) {
        return new ReadInstruction(astContext, List.of(result, getMemory(), getIndex()), getParams());
    }

    public final LogicVariable getMemory() {
        return (LogicVariable) getArg(1);
    }

    public final LogicValue getIndex() {
        return (LogicValue) getArg(2);
    }
}
