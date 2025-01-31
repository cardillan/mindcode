package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
public class GetlinkInstruction extends BaseResultInstruction {

    GetlinkInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.GETLINK, args, params);
    }

    protected GetlinkInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public GetlinkInstruction copy() {
        return new GetlinkInstruction(this, astContext, sideEffects);
    }

    @Override
    public GetlinkInstruction withContext(AstContext astContext) {
        return Objects.equals(this.astContext, astContext) ? this : new GetlinkInstruction(this, astContext, sideEffects);
    }

    @Override
    public GetlinkInstruction withSideEffects(SideEffects sideEffects) {
        return Objects.equals(this.sideEffects, sideEffects) ? this : new GetlinkInstruction(this, astContext, sideEffects);
    }

    @Override
    public GetlinkInstruction withResult(LogicVariable result) {
        assert getArgumentTypes() != null;
        return new GetlinkInstruction(astContext, List.of(result, getIndex()), getArgumentTypes());
    }

    public final LogicValue getIndex() {
        return (LogicValue) getArg(1);
    }
}
