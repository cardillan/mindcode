package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class UnpackColorInstruction extends BaseInstruction {
    UnpackColorInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.UNPACKCOLOR, args, params);
    }

    protected UnpackColorInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public UnpackColorInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new UnpackColorInstruction(this, astContext);
    }

    public final LogicValue getR() {
        return (LogicValue) getArg(0);
    }

    public final LogicValue getG() {
        return (LogicValue) getArg(1);
    }

    public final LogicValue getB() {
        return (LogicValue) getArg(2);
    }

    public final LogicValue getA() {
        return (LogicValue) getArg(3);
    }

    public final LogicValue getColor() {
        return (LogicValue) getArg(4);
    }
}
