package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.*;

import java.util.List;

public class SetInstruction extends BaseInstruction implements LogicResultInstruction {

    SetInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.SET, args, params);
    }

    protected SetInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public SetInstruction copy() {
        return new SetInstruction(this, astContext);
    }

    @Override
    public SetInstruction withContext(AstContext astContext) {
        return new SetInstruction(this, astContext);
    }

    @Override
    public SetInstruction withResult(LogicVariable result) {
        return new SetInstruction(astContext, List.of(result, getValue()), getParams());
    }

    public SetInstruction withValue(LogicValue value) {
        return new SetInstruction(astContext, List.of(getResult(), value), getParams());
    }

    @Override
    public final LogicVariable getResult() {
        return (LogicVariable) getArg(0);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(1);
    }
}
