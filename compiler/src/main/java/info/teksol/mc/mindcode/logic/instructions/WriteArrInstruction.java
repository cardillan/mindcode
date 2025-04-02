package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
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
public class WriteArrInstruction extends BaseInstruction implements ArrayAccessInstruction {
    private @Nullable ArrayConstructor arrayConstructor;

    WriteArrInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.WRITEARR, args, params);
    }

    protected WriteArrInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    protected void updateInfo(Set<InstructionInfo> types) {
        if (types.contains(InstructionInfo.ARRAY_ORGANIZATION) && !astContext.matches(AstSubcontextType.MOCK)) {
            arrayConstructor = getArrayOrganization().getExpander(this);
            // Changing array organization alters side effects
            setSideEffects(arrayConstructor.createSideEffects(getAccessType()));
        }
    }

    @Override
    public AccessType getAccessType() {
        return AccessType.WRITE;
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(0);
    }

    @Override
    public ArrayConstructor getArrayConstructor() {
        assert arrayConstructor != null;
        return arrayConstructor;
    }

    @Override
    public WriteArrInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new WriteArrInstruction(this, astContext);
    }

    @Override
    public int getRealSize(@Nullable Map<String, Integer> sharedStructures) {
        assert arrayConstructor != null;
        return arrayConstructor.getInstructionSize(getAccessType(), sharedStructures);
    }
}
