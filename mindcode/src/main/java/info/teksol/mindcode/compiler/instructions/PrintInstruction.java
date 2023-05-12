package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class PrintInstruction extends BaseInstruction {

    PrintInstruction(List<LogicArgument> args, List<LogicParameter> params) {
        super(Opcode.PRINT, args, params);
    }

    public PrintInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    public PrintInstruction withMarker(String marker) {
        return new PrintInstruction(this, marker);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(0);
    }
}
