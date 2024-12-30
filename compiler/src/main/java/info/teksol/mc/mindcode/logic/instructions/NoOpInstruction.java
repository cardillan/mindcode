package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;

import java.util.List;

public class NoOpInstruction extends BaseInstruction {

    NoOpInstruction(AstContext astContext) {
        super(astContext, Opcode.NOOP, List.of(), List.of());
    }

    protected NoOpInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public NoOpInstruction copy() {
        return new NoOpInstruction(this, astContext);
    }

    @Override
    public NoOpInstruction withContext(AstContext astContext) {
        return new NoOpInstruction(this, astContext);
    }
}
