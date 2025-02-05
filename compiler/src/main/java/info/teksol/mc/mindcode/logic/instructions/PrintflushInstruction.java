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
public class PrintflushInstruction extends BaseInstruction {

    PrintflushInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.PRINTFLUSH, args, params);
    }

    protected PrintflushInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public PrintflushInstruction copy() {
        return new PrintflushInstruction(this, astContext, sideEffects);
    }

    @Override
    public PrintflushInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new PrintflushInstruction(this, astContext, sideEffects);
    }

    @Override
    public PrintflushInstruction withSideEffects(SideEffects sideEffects) {
        return this.sideEffects == sideEffects ? this : new PrintflushInstruction(this, astContext, sideEffects);
    }


    public final LogicValue getBlock() {
        return (LogicValue) getArg(0);
    }
}
