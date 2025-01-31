package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@NullMarked
public class NoOpInstruction extends BaseInstruction {

    NoOpInstruction(AstContext astContext) {
        super(astContext, Opcode.NOOP, List.of(), List.of());
    }

    protected NoOpInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public NoOpInstruction copy() {
        return new NoOpInstruction(this, astContext, sideEffects);
    }

    @Override
    public NoOpInstruction withContext(AstContext astContext) {
        return Objects.equals(this.astContext, astContext) ? this : new NoOpInstruction(this, astContext, sideEffects);
    }

    @Override
    public NoOpInstruction withSideEffects(SideEffects sideEffects) {
        return Objects.equals(this.sideEffects, sideEffects) ? this : new NoOpInstruction(this, astContext, sideEffects);
    }
}
