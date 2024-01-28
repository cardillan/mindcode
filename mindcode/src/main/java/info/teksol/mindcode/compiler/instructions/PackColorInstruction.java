package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.*;

import java.util.List;

public class PackColorInstruction extends BaseInstruction implements LogicResultInstruction {
    PackColorInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.PACKCOLOR, args, params);
    }

    protected PackColorInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public PackColorInstruction copy() {
        return new PackColorInstruction(this, astContext);
    }

    @Override
    public PackColorInstruction withContext(AstContext astContext) {
        return new PackColorInstruction(this, astContext);
    }

    public PackColorInstruction withResult(LogicVariable result) {
        return new PackColorInstruction(astContext, List.of(result, getR(), getG(), getB(), getA()), getParams());
    }

    @Override
    public final LogicVariable getResult() {
        return (LogicVariable) getArg(0);
    }

    public final LogicValue getR() {
        return (LogicValue) getArg(1);
    }

    public final LogicValue getG() {
        return (LogicValue) getArg(2);
    }

    public final LogicValue getB() {
        return (LogicValue) getArg(3);
    }

    public final LogicValue getA() {
        return (LogicValue) getArg(4);
    }
}
