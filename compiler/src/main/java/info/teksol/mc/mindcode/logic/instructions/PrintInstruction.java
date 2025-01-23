package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class PrintInstruction extends BaseInstruction {

    PrintInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
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
