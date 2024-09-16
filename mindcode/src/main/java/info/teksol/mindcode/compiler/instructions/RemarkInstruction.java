package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class RemarkInstruction extends BaseInstruction {

    RemarkInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.REMARK, args, params);
    }

    private RemarkInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public RemarkInstruction copy() {
        return new RemarkInstruction(this, astContext);
    }

    @Override
    public RemarkInstruction withContext(AstContext astContext) {
        return new RemarkInstruction(this, astContext);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(0);
    }

    @Override
    public int getRealSize() {
        return switch (astContext.getProfile().getRemarks()) {
            case NONE -> 0;
            case ACTIVE -> 1;
            case PASSIVE -> 2;
        };
    }
}
