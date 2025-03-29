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
public class GetlinkInstruction extends BaseResultInstruction {

    GetlinkInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.GETLINK, args, params);
    }

    protected GetlinkInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public GetlinkInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new GetlinkInstruction(this, astContext);
    }

    @Override
    public GetlinkInstruction withResult(LogicVariable result) {
        assert getArgumentTypes() != null;
        return new GetlinkInstruction(astContext, List.of(result, getIndex()), getArgumentTypes());
    }

    public final LogicValue getIndex() {
        return (LogicValue) getArg(1);
    }
}
