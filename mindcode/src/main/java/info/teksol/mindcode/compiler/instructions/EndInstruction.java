package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class EndInstruction extends BaseInstruction {

    EndInstruction(AstContext astContext) {
        super(astContext, Opcode.END, List.of(), List.of());
    }

    protected EndInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    @Override
    public EndInstruction copy() {
        return new EndInstruction(this, marker);
    }
    public EndInstruction withMarker(String marker) {
        return new EndInstruction(this, marker);
    }
}
