package io.github.cardillan.mc.mindcode.logic.instructions;

import io.github.cardillan.mc.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class StopInstruction extends BaseInstruction {

    StopInstruction(AstContext astContext) {
        super(astContext, Opcode.STOP, List.of(), List.of());
    }

    protected StopInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public StopInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new StopInstruction(this, astContext);
    }

    @Override
    public boolean endsCodePath() {
        return true;
    }
}
