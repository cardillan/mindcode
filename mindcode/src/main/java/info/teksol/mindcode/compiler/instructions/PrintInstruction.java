package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class PrintInstruction extends BaseInstruction {

    PrintInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.PRINT, args, params);
    }

    private PrintInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public PrintInstruction copy() {
        return new PrintInstruction(this, astContext);
    }

    @Override
    public PrintInstruction withContext(AstContext astContext) {
        return new PrintInstruction(this, astContext);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(0);
    }
}
