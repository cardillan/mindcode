package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.*;

import java.util.List;

public class ReadInstruction extends BaseInstruction implements LogicResultInstruction {

    ReadInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
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

    public ReadInstruction withResult(LogicVariable result) {
        return new ReadInstruction(astContext, List.of(result, getMemory(), getIndex()), getParams());
    }

    @Override
    public final LogicVariable getResult() {
        return (LogicVariable) getArg(0);
    }

    public final LogicVariable getMemory() {
        return (LogicVariable) getArg(1);
    }

    public final LogicValue getIndex() {
        return (LogicValue) getArg(2);
    }
}
