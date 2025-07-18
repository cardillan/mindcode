package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class EmptyInstruction extends BaseInstruction {

    EmptyInstruction(AstContext astContext) {
        super(astContext, Opcode.EMPTY, List.of(), List.of());
    }

    protected EmptyInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public EmptyInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new EmptyInstruction(this, astContext);
    }

}
