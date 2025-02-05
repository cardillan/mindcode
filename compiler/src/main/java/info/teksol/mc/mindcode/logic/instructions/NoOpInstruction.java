package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;

import java.util.List;

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
        return this.astContext == astContext ? this : new NoOpInstruction(this, astContext, sideEffects);
    }

    @Override
    public NoOpInstruction withSideEffects(SideEffects sideEffects) {
        return this.sideEffects == sideEffects ? this : new NoOpInstruction(this, astContext, sideEffects);
    }
}
