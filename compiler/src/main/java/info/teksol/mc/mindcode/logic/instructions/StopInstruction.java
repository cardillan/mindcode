package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class StopInstruction extends BaseInstruction {

    StopInstruction(AstContext astContext) {
        super(astContext, Opcode.STOP, List.of(), List.of());
    }

    protected StopInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public StopInstruction copy() {
        return new StopInstruction(this, astContext, sideEffects);
    }

    @Override
    public StopInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new StopInstruction(this, astContext, sideEffects);
    }

    @Override
    public StopInstruction withSideEffects(SideEffects sideEffects) {
        return this.sideEffects == sideEffects ? this : new StopInstruction(this, astContext, sideEffects);
    }

    @Override
    public boolean endsCodePath() {
        return true;
    }
}
