package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;

import java.util.List;

public class PrintflushInstruction extends BaseInstruction {

    PrintflushInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
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

    public final LogicValue getBlock() {
        return (LogicValue) getArg(0);
    }
}
