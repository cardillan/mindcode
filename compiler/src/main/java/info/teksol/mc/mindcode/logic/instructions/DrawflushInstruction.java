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
public class DrawflushInstruction extends BaseInstruction {

    DrawflushInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.DRAWFLUSH, args, params);
    }

    protected DrawflushInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public DrawflushInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new DrawflushInstruction(this, astContext);
    }


    public final LogicValue getDisplay() {
        return (LogicValue) getArg(0);
    }
}
