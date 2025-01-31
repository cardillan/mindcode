package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicKeyword;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
public class LookupInstruction extends BaseResultInstruction {

    LookupInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.LOOKUP, args, params);
    }

    protected LookupInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public LookupInstruction copy() {
        return new LookupInstruction(this, astContext, sideEffects);
    }

    @Override
    public LookupInstruction withContext(AstContext astContext) {
        return Objects.equals(this.astContext, astContext) ? this : new LookupInstruction(this, astContext, sideEffects);
    }

    @Override
    public LookupInstruction withSideEffects(SideEffects sideEffects) {
        return Objects.equals(this.sideEffects, sideEffects) ? this : new LookupInstruction(this, astContext, sideEffects);
    }

    @Override
    public LookupInstruction withResult(LogicVariable result) {
        assert getArgumentTypes() != null;
        return new LookupInstruction(astContext, List.of(getType(), result, getIndex()), getArgumentTypes());
    }

    public final LogicKeyword getType() {
        return (LogicKeyword) getArg(0);
    }

    public final LogicValue getIndex() {
        return (LogicValue) getArg(2);
    }
}
