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
public class WriteArrInstruction extends BaseInstruction implements ArrayAccessInstruction {

    WriteArrInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.WRITEARR, args, params);
    }

    protected WriteArrInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public String getJumpTableId() {
        return getArray().getWriteJumpTableId();
    }

    @Override
    public WriteArrInstruction copy() {
        return new WriteArrInstruction(this, astContext);
    }

    @Override
    public WriteArrInstruction withContext(AstContext astContext) {
        return new WriteArrInstruction(this, astContext);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(0);
    }
}
