package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class PrintflushInstruction extends BaseInstruction {

    PrintflushInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params, String marker) {
        super(astContext, Opcode.PRINTFLUSH, args, params, marker);
    }

    protected PrintflushInstruction(BaseInstruction other, AstContext astContext, String marker) {
        super(other, astContext, marker);
    }

    @Override
    public PrintflushInstruction copy() {
        return new PrintflushInstruction(this, astContext, marker);
    }

    public PrintflushInstruction withMarker(String marker) {
        return new PrintflushInstruction(this, astContext, marker);
    }

    @Override
    public PrintflushInstruction withContext(AstContext astContext) {
        return new PrintflushInstruction(this, astContext, marker);
    }

    public final LogicVariable getBlock() {
        return (LogicVariable) getArg(0);
    }
}
