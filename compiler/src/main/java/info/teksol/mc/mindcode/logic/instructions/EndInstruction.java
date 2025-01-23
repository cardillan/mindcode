package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
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
    public EndInstruction copy() {
        return new EndInstruction(this, astContext);
    }

    @Override
    public EndInstruction withContext(AstContext astContext) {
        return new EndInstruction(this, astContext);
    }

    @Override
    public boolean endsCodePath() {
        return true;
    }
}
