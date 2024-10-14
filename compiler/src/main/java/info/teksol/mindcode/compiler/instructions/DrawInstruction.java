package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.*;

import java.util.List;

public class DrawInstruction extends BaseInstruction {

    DrawInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.DRAW, args, params);
    }

    protected DrawInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public DrawInstruction copy() {
        return new DrawInstruction(this, astContext);
    }

    @Override
    public DrawInstruction withContext(AstContext astContext) {
        return new DrawInstruction(this, astContext);
    }

    public final LogicKeyword getType() {
        return (LogicKeyword) getArg(0);
    }

    public final LogicValue getX() {
        return (LogicValue) getArg(1);
    }

    public final LogicValue getY() {
        return (LogicValue) getArg(2);
    }

    public final LogicValue getP1() {
        return (LogicValue) getArg(3);
    }

    public final LogicValue getP2() {
        return (LogicValue) getArg(4);
    }

    public final LogicValue getP3() {
        return (LogicValue) getArg(5);
    }

    public final LogicValue getP4() {
        return (LogicValue) getArg(6);
    }
}
