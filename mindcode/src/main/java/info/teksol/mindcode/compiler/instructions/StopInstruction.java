package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class StopInstruction extends BaseInstruction {

    StopInstruction(AstContext astContext, String marker) {
        super(astContext, Opcode.STOP, List.of(), List.of(), marker);
    }

    protected StopInstruction(BaseInstruction other, AstContext astContext, String marker) {
        super(other, astContext, marker);
    }

    @Override
    public StopInstruction copy() {
        return new StopInstruction(this, astContext, marker);
    }

    public StopInstruction withMarker(String marker) {
        return new StopInstruction(this, astContext, marker);
    }

    @Override
    public StopInstruction withContext(AstContext astContext) {
        return new StopInstruction(this, astContext, marker);
    }
}
