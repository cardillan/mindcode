package io.github.cardillan.mindcode.logic.instructions;

import io.github.cardillan.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mindcode.compiler.astcontext.AstSubcontextType;
import io.github.cardillan.mindcode.logic.arguments.LogicArgument;
import io.github.cardillan.mindcode.logic.arguments.LogicArray;
import io.github.cardillan.mindcode.logic.arguments.LogicVariable;
import io.github.cardillan.mindcode.logic.arguments.arrays.ArrayConstructor;
import io.github.cardillan.mindcode.logic.arguments.arrays.ArrayConstructorFactory;
import io.github.cardillan.mindcode.logic.opcodes.InstructionParameterType;
import io.github.cardillan.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.github.cardillan.mindcode.logic.instructions.InstructionInfo.*;

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
    public  ArrayAccessInstruction setArrayOrganization(ArrayOrganization arrayOrganization, ArrayConstruction arrayConstruction) {
        info.put(ARRAY_ORGANIZATION, arrayOrganization);
        info.put(ARRAY_CONSTRUCTION, arrayConstruction);
        updateInfo(Set.of(ARRAY_ORGANIZATION, ARRAY_CONSTRUCTION));
        return this;
    }

    // NOTE: the same code must be present in ReadArrInstrution and WriteArrInstruction
    private static final Set<InstructionInfo> updateConstruction = Set.of(ARRAY_ORGANIZATION, ARRAY_CONSTRUCTION, ARRAY_INJECTIONS,
            ARRAY_INJECTED_INTO, COMPACT_ACCESS_SOURCE, COMPACT_ACCESS_TARGET);

    @Override
    protected void updateInfo(Set<InstructionInfo> types) {
        if (types.stream().anyMatch(updateConstruction::contains) && !astContext.matches(AstSubcontextType.MOCK)) {
            arrayConstructor = ArrayConstructorFactory.create(this);
            // Changing array organization alters side effects
            setSideEffects(arrayConstructor.createSideEffects());
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
    public ReadArrInstruction withArray(LogicArray array) {
        assert getArgumentTypes() != null;
        return new ReadArrInstruction(astContext, List.of(getResult(), array, getIndex()), getArgumentTypes()).copyInfo(this);
    }

    @Override
    public int getSharedSize(@Nullable Map<String, Integer> sharedStructures) {
        assert arrayConstructor != null;
        return arrayConstructor.getInstructionSize(sharedStructures);
    }
}
