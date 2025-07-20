package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class BaseResultInstruction extends BaseInstruction implements LogicResultInstruction {
    private final int resultIndex;

    BaseResultInstruction(AstContext astContext, Opcode opcode, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, opcode, args, params);
        resultIndex = params == null ? -1 : params.indexOf(InstructionParameterType.RESULT);
    }

    public BaseResultInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
        assert other.getArgumentTypes() != null;
        resultIndex = other.getArgumentTypes().indexOf(InstructionParameterType.RESULT);
    }

    @Override
    public BaseResultInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new BaseResultInstruction(this, astContext).copyInfo(this);
    }

    @Override
    public LogicVariable getResult() {
        return (LogicVariable) getArg(resultIndex);
    }

    public LogicArgument getResultArgument() {
        return getArg(resultIndex);
    }

    @Override
    public LogicResultInstruction withResult(LogicVariable result) {
        List<LogicArgument> args = new ArrayList<>(getArgs());
        args.set(resultIndex, result);
        assert getArgumentTypes() != null;
        return new BaseResultInstruction(astContext, getOpcode(), args, getArgumentTypes()).copyInfo(this);
    }
}
