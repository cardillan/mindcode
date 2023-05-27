package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class EndInstruction extends BaseInstruction {

    EndInstruction(AstContext astContext, String marker) {
        super(astContext, Opcode.END, List.of(), List.of(), marker);
    }

    protected EndInstruction(BaseInstruction other, AstContext astContext, String marker) {
        super(other, astContext, marker);
    }

    @Override
    public EndInstruction copy() {
        return new EndInstruction(this, astContext, marker);
    }

    public EndInstruction withMarker(String marker) {
        return new EndInstruction(this, astContext, marker);
    }

    @Override
    public EndInstruction withContext(AstContext astContext) {
        return new EndInstruction(this, astContext, marker);
    }
}
