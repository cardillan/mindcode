package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@NullMarked
public class RemarkInstruction extends BaseInstruction {

    RemarkInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.REMARK, args, params);
    }

    protected RemarkInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public RemarkInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new RemarkInstruction(this, astContext);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(0);
    }

    @Override
    public int getRealSize(@Nullable Map<String, Integer> sharedStructures) {
        return switch (astContext.getProfile().getRemarks()) {
            case NONE, COMMENTS -> 0;
            case ACTIVE -> 1;
            case PASSIVE -> 2;
        };
    }
}
