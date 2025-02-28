package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class InitVarInstruction extends BaseInstruction {
    InitVarInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.INITVAR, args, params);
    }

    protected InitVarInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public InitVarInstruction copy() {
        return new InitVarInstruction(this, astContext, sideEffects);
    }

    @Override
    public InitVarInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new InitVarInstruction(this, astContext, sideEffects);
    }

    @Override
    public InitVarInstruction withSideEffects(SideEffects sideEffects) {
        return this.sideEffects == sideEffects ? this : new InitVarInstruction(this, astContext, sideEffects);
    }
}
