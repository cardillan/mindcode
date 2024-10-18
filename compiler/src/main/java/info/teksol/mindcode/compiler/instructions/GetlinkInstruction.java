package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.*;

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
