package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@NullMarked
public class EndInstruction extends BaseInstruction {

    EndInstruction(AstContext astContext) {
        super(astContext, Opcode.END, List.of(), List.of());
    }

    protected EndInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public EndInstruction copy() {
        return new EndInstruction(this, astContext, sideEffects);
    }

    @Override
    public EndInstruction withContext(AstContext astContext) {
        return Objects.equals(this.astContext, astContext) ? this : new EndInstruction(this, astContext, sideEffects);
    }

    @Override
    public EndInstruction withSideEffects(SideEffects sideEffects) {
        return Objects.equals(this.sideEffects, sideEffects) ? this : new EndInstruction(this, astContext, sideEffects);
    }

    @Override
    public boolean endsCodePath() {
        return true;
    }
}
