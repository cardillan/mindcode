package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@NullMarked
public class ErrorInstruction extends BaseInstruction {

    public ErrorInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.ERROR, args, params);
    }

    public ErrorInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    protected void validate() {
        // Do nothing
    }

    @Override
    public boolean endsCodePath() {
        return true;
    }

    @Override
    public ErrorInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new ErrorInstruction(this, astContext);
    }

    @Override
    public int getSharedSize(@Nullable Map<String, Integer> sharedStructures) {
        return switch (getLocalProfile().getErrorReporting()) {
            case NONE -> 0;
            case ASSERT -> 1;
            case MINIMAL, SIMPLE, DESCRIBED -> args.size() + 1;
        };
    }
}
