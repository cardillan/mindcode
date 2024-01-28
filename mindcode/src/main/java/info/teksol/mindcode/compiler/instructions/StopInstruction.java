package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class StopInstruction extends BaseInstruction {

    StopInstruction(AstContext astContext) {
        super(astContext, Opcode.STOP, List.of(), List.of());
    }

    protected StopInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public StopInstruction copy() {
        return new StopInstruction(this, astContext);
    }

    @Override
    public StopInstruction withContext(AstContext astContext) {
        return new StopInstruction(this, astContext);
    }
}
