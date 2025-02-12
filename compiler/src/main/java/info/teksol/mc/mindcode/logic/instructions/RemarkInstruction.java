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
public class RemarkInstruction extends BaseInstruction {

    RemarkInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
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
