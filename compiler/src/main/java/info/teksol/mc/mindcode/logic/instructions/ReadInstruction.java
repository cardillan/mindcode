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
public class ReadInstruction extends BaseResultInstruction {

    ReadInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.READ, args, params);
    }

    protected ReadInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public ReadInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new ReadInstruction(this, astContext);
    }

    @Override
    public ReadInstruction withResult(LogicVariable result) {
        assert getArgumentTypes() != null;
        return new ReadInstruction(astContext, List.of(result, getMemory(), getIndex()), getArgumentTypes());
    }

    public final LogicValue getMemory() {
        return (LogicValue) getArg(1);
    }

    public final LogicValue getIndex() {
        return (LogicValue) getArg(2);
    }
}
