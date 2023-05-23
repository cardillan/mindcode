package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class PrintInstruction extends BaseInstruction {

    PrintInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.PRINT, args, params);
    }

    public PrintInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    @Override
    public PrintInstruction copy() {
        return new PrintInstruction(this, marker);
    }
    public PrintInstruction withMarker(String marker) {
        return new PrintInstruction(this, marker);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(0);
    }
}
