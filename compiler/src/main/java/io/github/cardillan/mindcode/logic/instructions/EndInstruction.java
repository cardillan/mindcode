package io.github.cardillan.mindcode.logic.instructions;

import io.github.cardillan.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class EndInstruction extends BaseInstruction {

    EndInstruction(AstContext astContext) {
        super(astContext, Opcode.END, List.of(), List.of());
    }

    protected EndInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public EndInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new EndInstruction(this, astContext);
    }

    @Override
    public boolean endsCodePath() {
        return true;
    }
}
