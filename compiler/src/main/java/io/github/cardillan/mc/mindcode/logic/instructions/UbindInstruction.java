package io.github.cardillan.mc.mindcode.logic.instructions;

import io.github.cardillan.mc.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicArgument;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicBuiltIn;
import io.github.cardillan.mc.mindcode.logic.opcodes.InstructionParameterType;
import io.github.cardillan.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class UbindInstruction extends BaseInstruction {
    private static final SideEffects UNIT = SideEffects.writes(LogicBuiltIn.UNIT);

    UbindInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.UBIND, args, params);
    }

    protected UbindInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public UbindInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new UbindInstruction(this, astContext);
    }

    @Override
    public SideEffects getSideEffects() {
        return super.getSideEffects().plusWrites(List.of(LogicBuiltIn.UNIT));
    }
}
