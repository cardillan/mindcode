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
public class PrintCharInstruction extends BaseInstruction implements PrintingInstruction {

    PrintCharInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.PRINTCHAR, args, params);
    }

    protected PrintCharInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public PrintCharInstruction copy() {
        return new PrintCharInstruction(this, astContext, sideEffects);
    }

    @Override
    public PrintCharInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new PrintCharInstruction(this, astContext, sideEffects);
    }

    @Override
    public PrintCharInstruction withSideEffects(SideEffects sideEffects) {
        return this.sideEffects == sideEffects ? this : new PrintCharInstruction(this, astContext, sideEffects);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(0);
    }
}
