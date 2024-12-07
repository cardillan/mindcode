package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BaseResultInstruction extends BaseInstruction implements LogicResultInstruction {
    private final int resultIndex;

    BaseResultInstruction(AstContext astContext, Opcode opcode, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, opcode, args, params);
        resultIndex = params.indexOf(InstructionParameterType.RESULT);
    }

    public BaseResultInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
        resultIndex = other.getArgumentTypes().indexOf(InstructionParameterType.RESULT);
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
        return new BaseResultInstruction(astContext, getOpcode(), args, getArgumentTypes());
    }
}
