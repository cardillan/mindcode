package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;

import java.util.List;

public class GetlinkInstruction extends BaseResultInstruction {

    GetlinkInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.GETLINK, args, params);
    }

    protected GetlinkInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public GetlinkInstruction copy() {
        return new GetlinkInstruction(this, astContext);
    }

    @Override
    public GetlinkInstruction withContext(AstContext astContext) {
        return new GetlinkInstruction(this, astContext);
    }

    @Override
    public GetlinkInstruction withResult(LogicVariable result) {
        return new GetlinkInstruction(astContext, List.of(result, getIndex()), getArgumentTypes());
    }

    public final LogicValue getIndex() {
        return (LogicValue) getArg(1);
    }
}
