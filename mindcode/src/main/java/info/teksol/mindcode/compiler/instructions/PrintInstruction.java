package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class PrintInstruction extends BaseInstruction {

    PrintInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params, String marker) {
        super(astContext, Opcode.PRINT, args, params, marker);
    }

    private PrintInstruction(BaseInstruction other, AstContext astContext, String marker) {
        super(other, astContext, marker);
    }

    @Override
    public PrintInstruction copy() {
        return new PrintInstruction(this, astContext, marker);
    }

    public PrintInstruction withMarker(String marker) {
        return new PrintInstruction(this, astContext, marker);
    }

    @Override
    public PrintInstruction withContext(AstContext astContext) {
        return new PrintInstruction(this, astContext, marker);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(0);
    }
}
