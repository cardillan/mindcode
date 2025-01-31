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
public class PushInstruction extends BaseInstruction implements PushOrPopInstruction {

    PushInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.PUSH, args, params);
    }

    protected PushInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public PushInstruction copy() {
        return new PushInstruction(this, astContext, sideEffects);
    }

    @Override
    public PushInstruction withContext(AstContext astContext) {
        return Objects.equals(this.astContext, astContext) ? this : new PushInstruction(this, astContext, sideEffects);
    }

    @Override
    public PushInstruction withSideEffects(SideEffects sideEffects) {
        return Objects.equals(this.sideEffects, sideEffects) ? this : new PushInstruction(this, astContext, sideEffects);
    }
}
