package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.arguments.arrays.ArrayConstructor;
import info.teksol.mc.mindcode.logic.arguments.arrays.ArrayConstructor.AccessType;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@NullMarked
public class ReadArrInstruction extends BaseResultInstruction implements ArrayAccessInstruction {
    private @Nullable ArrayConstructor arrayConstructor;

    ReadArrInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.READARR, args, params);
    }

    protected ReadArrInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    protected void updateInfo(Set<InstructionInfo> types) {
        if (types.contains(InstructionInfo.ARRAY_ORGANIZATION) || types.contains(InstructionInfo.ARRAY_CONSTRUCTION)
                && !astContext.matches(AstSubcontextType.MOCK)) {
            arrayConstructor = getArrayOrganization().getConstructor(this);
            // Changing array organization alters side effects
            setSideEffects(arrayConstructor.createSideEffects(getAccessType()));
        }
    }

    @Override
    public AccessType getAccessType() {
        return AccessType.READ;
    }

    @Override
    public ArrayConstructor getArrayConstructor() {
        assert arrayConstructor != null;
        return arrayConstructor;
    }

    @Override
    public ReadArrInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new ReadArrInstruction(this, astContext);
    }

    @Override
    public ReadArrInstruction withResult(LogicVariable result) {
        assert getArgumentTypes() != null;
        return new ReadArrInstruction(astContext, List.of(result, getArray(), getIndex()), getArgumentTypes()).copyInfo(this);
    }

    @Override
    public int getRealSize(@Nullable Map<String, Integer> sharedStructures) {
        assert arrayConstructor != null;
        return arrayConstructor.getInstructionSize(getAccessType(), sharedStructures);
    }
}
