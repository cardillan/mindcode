package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

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
        return Objects.equals(this.astContext, astContext) ? this : new StopInstruction(this, astContext, sideEffects);
    }

    @Override
    public StopInstruction withSideEffects(SideEffects sideEffects) {
        return Objects.equals(this.sideEffects, sideEffects) ? this : new StopInstruction(this, astContext, sideEffects);
    }

    @Override
    public boolean endsCodePath() {
        return true;
    }
}
