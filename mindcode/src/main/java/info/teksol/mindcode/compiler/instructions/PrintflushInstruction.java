package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class PrintflushInstruction extends BaseInstruction {

    PrintflushInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.PRINTFLUSH, args, params);
    }

    protected PrintflushInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    @Override
    public PrintflushInstruction copy() {
        return new PrintflushInstruction(this, marker);
    }

    public PrintflushInstruction withMarker(String marker) {
        return new PrintflushInstruction(this, marker);
    }

    public final LogicVariable getBlock() {
        return (LogicVariable) getArg(0);
    }
}
