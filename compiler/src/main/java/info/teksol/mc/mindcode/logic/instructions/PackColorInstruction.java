package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class PackColorInstruction extends BaseResultInstruction {
    PackColorInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
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

    @Override
    public PackColorInstruction withResult(LogicVariable result) {
        assert getArgumentTypes() != null;
        return new PackColorInstruction(astContext, List.of(result, getR(), getG(), getB(), getA()), getArgumentTypes());
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
