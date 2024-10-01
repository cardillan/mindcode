package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.InstructionParameterType;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.Opcode;

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
