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
public class FormatInstruction extends BaseInstruction {

    FormatInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.FORMAT, args, params);
    }

    protected FormatInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public FormatInstruction copy() {
        return new FormatInstruction(this, astContext, sideEffects);
    }

    @Override
    public FormatInstruction withContext(AstContext astContext) {
        return Objects.equals(this.astContext, astContext) ? this : new FormatInstruction(this, astContext, sideEffects);
    }

    @Override
    public FormatInstruction withSideEffects(SideEffects sideEffects) {
        return Objects.equals(this.sideEffects, sideEffects) ? this : new FormatInstruction(this, astContext, sideEffects);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(0);
    }
}
