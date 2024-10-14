package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.Opcode;

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
