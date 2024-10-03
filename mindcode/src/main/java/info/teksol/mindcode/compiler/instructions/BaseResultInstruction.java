package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.InstructionParameterType;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BaseResultInstruction extends BaseInstruction implements LogicResultInstruction {
    private final int resultIndex;

    public BaseResultInstruction(AstContext astContext, Opcode opcode, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, opcode, args, params);
        resultIndex = params.indexOf(InstructionParameterType.RESULT);
    }

    public BaseResultInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
        resultIndex = other.getParams().indexOf(InstructionParameterType.RESULT);
    }

    @Override
    public BaseResultInstruction copy() {
        return new BaseResultInstruction(this, astContext);
    }

    @Override
    public BaseResultInstruction withContext(AstContext astContext) {
        return Objects.equals(this.astContext, astContext) ? this : new BaseResultInstruction(this, astContext);
    }

    @Override
    public LogicVariable getResult() {
        return (LogicVariable) getArg(resultIndex);
    }

    @Override
    public LogicResultInstruction withResult(LogicVariable result) {
        List<LogicArgument> args = new ArrayList<>(getArgs());
        args.set(resultIndex, result);
        return new BaseResultInstruction(astContext, getOpcode(), args, getParams());
    }
}
