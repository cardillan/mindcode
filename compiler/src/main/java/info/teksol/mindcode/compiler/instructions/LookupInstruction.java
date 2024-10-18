package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.*;

import java.util.List;

public class LookupInstruction extends BaseResultInstruction {

    LookupInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.LOOKUP, args, params);
    }

    protected LookupInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public LookupInstruction copy() {
        return new LookupInstruction(this, astContext);
    }

    @Override
    public LookupInstruction withContext(AstContext astContext) {
        return new LookupInstruction(this, astContext);
    }

    @Override
    public LookupInstruction withResult(LogicVariable result) {
        return new LookupInstruction(astContext, List.of(getType(), result, getIndex()), getArgumentTypes());
    }

    public final LogicKeyword getType() {
        return (LogicKeyword) getArg(0);
    }

    public final LogicValue getIndex() {
        return (LogicValue) getArg(2);
    }
}
