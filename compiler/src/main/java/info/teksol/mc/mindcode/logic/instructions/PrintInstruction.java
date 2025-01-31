package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
public class PrintInstruction extends BaseInstruction {

    PrintInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.PRINT, args, params);
    }

    protected PrintInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public PrintInstruction copy() {
        return new PrintInstruction(this, astContext, sideEffects);
    }

    @Override
    public PrintInstruction withContext(AstContext astContext) {
        return Objects.equals(this.astContext, astContext) ? this : new PrintInstruction(this, astContext, sideEffects);
    }

    @Override
    public PrintInstruction withSideEffects(SideEffects sideEffects) {
        return Objects.equals(this.sideEffects, sideEffects) ? this : new PrintInstruction(this, astContext, sideEffects);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(0);
    }
}
