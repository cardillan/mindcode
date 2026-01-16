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
public class WaitInstruction extends BaseInstruction {

    WaitInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.WAIT, args, params);
    }

    protected WaitInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public WaitInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new WaitInstruction(this, astContext);
    }

    public WaitInstruction withTime(LogicValue time) {
        assert getArgumentTypes() != null;
        return new WaitInstruction(astContext, List.of(time), getArgumentTypes()).copyInfo(this);
    }

    public final LogicValue getTime() {
        return (LogicValue) getArg(0);
    }
}
