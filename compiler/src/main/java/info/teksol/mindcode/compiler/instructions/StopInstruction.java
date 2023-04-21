package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class StopInstruction extends BaseInstruction {

    StopInstruction() {
        super(Opcode.STOP, List.of(), List.of());
    }

    protected StopInstruction(BaseInstruction other, String marker) {
        super(other, marker);
    }

    public StopInstruction withMarker(String marker) {
        return new StopInstruction(this, marker);
    }
}
