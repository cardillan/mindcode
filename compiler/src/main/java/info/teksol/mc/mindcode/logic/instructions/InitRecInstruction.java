package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@NullMarked
public class InitRecInstruction extends BaseInstruction {

    InitRecInstruction(AstContext astContext) {
        super(astContext, Opcode.INITREC, List.of(), List.of());
    }

    protected InitRecInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public InitRecInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new InitRecInstruction(this, astContext);
    }

    @Override
    public int getSharedSize(@Nullable Map<String, Integer> sharedStructures) {
        return getFunction().getArrays().size();
    }
}
