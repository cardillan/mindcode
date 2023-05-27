package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class ReadInstruction extends BaseInstruction implements LogicResultInstruction {

    ReadInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params, String marker) {
        super(astContext, Opcode.READ, args, params, marker);
    }

    protected ReadInstruction(BaseInstruction other, AstContext astContext, String marker) {
        super(other, astContext, marker);
    }

    @Override
    public ReadInstruction copy() {
        return new ReadInstruction(this, astContext, marker);
    }

    @Override
    public ReadInstruction withMarker(String marker) {
        return new ReadInstruction(this, astContext, marker);
    }

    @Override
    public ReadInstruction withContext(AstContext astContext) {
        return new ReadInstruction(this, astContext, marker);
    }

    public ReadInstruction withResult(LogicVariable result) {
        return new ReadInstruction(getAstContext(), List.of(result, getMemory(), getIndex()), getParams(), marker);
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
