package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
public class PopInstruction extends BaseInstruction implements PushOrPopInstruction {

    PopInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.POP, args, params);
    }

    protected PopInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public PopInstruction copy() {
        return new PopInstruction(this, astContext, sideEffects);
    }

    @Override
    public PopInstruction withContext(AstContext astContext) {
        return Objects.equals(this.astContext, astContext) ? this : new PopInstruction(this, astContext, sideEffects);
    }

    @Override
    public PopInstruction withSideEffects(SideEffects sideEffects) {
        return Objects.equals(this.sideEffects, sideEffects) ? this : new PopInstruction(this, astContext, sideEffects);
    }

}
