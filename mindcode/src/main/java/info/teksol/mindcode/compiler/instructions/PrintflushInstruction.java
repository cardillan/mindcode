package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicParameter;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class PrintflushInstruction extends BaseInstruction {

    PrintflushInstruction(AstContext astContext, List<LogicArgument> args, List<LogicParameter> params) {
        super(astContext, Opcode.PRINTFLUSH, args, params);
    }

    protected PrintflushInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public PrintflushInstruction copy() {
        return new PrintflushInstruction(this, astContext);
    }

    @Override
    public PrintflushInstruction withContext(AstContext astContext) {
        return new PrintflushInstruction(this, astContext);
    }

    public final LogicVariable getBlock() {
        return (LogicVariable) getArg(0);
    }
}
