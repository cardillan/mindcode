package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;

import java.util.List;

public class FormatInstruction extends BaseInstruction {

    FormatInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.FORMAT, args, params);
    }

    private FormatInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public FormatInstruction copy() {
        return new FormatInstruction(this, astContext);
    }

    @Override
    public FormatInstruction withContext(AstContext astContext) {
        return new FormatInstruction(this, astContext);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(0);
    }
}
