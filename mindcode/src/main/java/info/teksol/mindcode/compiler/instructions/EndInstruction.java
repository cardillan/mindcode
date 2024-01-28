package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class EndInstruction extends BaseInstruction {

    EndInstruction(AstContext astContext) {
        super(astContext, Opcode.END, List.of(), List.of());
    }

    protected EndInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public EndInstruction copy() {
        return new EndInstruction(this, astContext);
    }

    @Override
    public EndInstruction withContext(AstContext astContext) {
        return new EndInstruction(this, astContext);
    }
}
