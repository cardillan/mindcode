package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicBuiltIn;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
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
