package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicKeyword;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
public class DrawInstruction extends BaseInstruction {

    DrawInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.DRAW, args, params);
    }

    protected DrawInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public DrawInstruction copy() {
        return new DrawInstruction(this, astContext, sideEffects);
    }

    @Override
    public DrawInstruction withContext(AstContext astContext) {
        return Objects.equals(this.astContext, astContext) ? this : new DrawInstruction(this, astContext, sideEffects);
    }

    @Override
    public DrawInstruction withSideEffects(SideEffects sideEffects) {
        return Objects.equals(this.sideEffects, sideEffects) ? this : new DrawInstruction(this, astContext, sideEffects);
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
