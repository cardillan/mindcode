package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicArray;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class ReadArrInstruction extends BaseResultInstruction {

    ReadArrInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.READARR, args, params);
    }

    protected ReadArrInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public ReadArrInstruction copy() {
        return new ReadArrInstruction(this, astContext);
    }

    @Override
    public ReadArrInstruction withContext(AstContext astContext) {
        return new ReadArrInstruction(this, astContext);
    }

    @Override
    public ReadArrInstruction withResult(LogicVariable result) {
        assert getArgumentTypes() != null;
        return new ReadArrInstruction(astContext, List.of(result, getArray(), getIndex()), getArgumentTypes());
    }

    public final LogicArray getArray() {
        return (LogicArray) getArg(1);
    }

    public final LogicValue getIndex() {
        return (LogicValue) getArg(2);
    }
}
