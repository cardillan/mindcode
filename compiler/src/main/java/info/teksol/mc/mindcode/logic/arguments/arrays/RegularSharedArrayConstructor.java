package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.generation.variables.ArrayStore;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.compiler.generation.variables.RemoteVariable;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import info.teksol.mc.mindcode.logic.instructions.SideEffects;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@NullMarked
public abstract class RegularSharedArrayConstructor extends SharedArrayConstructor {
    protected final LogicVariable proc;
    protected final boolean shared;

    public RegularSharedArrayConstructor(ArrayConstructorContext context, ArrayAccessInstruction instruction,
            String indexSuffix, String returnSuffix, String elementSuffix) {
        super(context, instruction, indexSuffix, returnSuffix, elementSuffix);
        NameCreator nameCreator = context.nameCreator();

        String baseName = arrayStore.getName();
        proc = LogicVariable.arrayAccess(baseName, "*proc", nameCreator.arrayAccess(baseName, "proc"));
        shared = arrayStore.getArrayType() == ArrayStore.ArrayType.REMOTE_SHARED;
    }

    @Override
    public int getInstructionSize(@Nullable Map<String, Integer> sharedStructures) {
        computeSharedJumpTableSize(sharedStructures);
        int checkSize = boundsCheckSize();
        return folded()
                ? checkSize + 4 + flag(!useTextTables && !profile.isSymbolicLabels())
                : checkSize + 4 - flag(useTextTables) + flag(shared);
    }

    @Override
    public double getExecutionSteps() {
        // There is a possibility that further optimization will eliminate the transfer variable,
        // saving at least one instruction and execution step. We can't discount the instruction size safely,
        // but we can at least discount the execution step; this will also cause the regular array to be preferred
        // over a compact array, if there's enough instruction space left.
        return boundsCheckExecutionSteps() + 6
               - flag(useTextTables)
               + flag(folded() || shared)
               + flag(profile.isSymbolicLabels()) - 0.2;
    }

    @Override
    protected SideEffects createCallSideEffects() {
        return createSideEffects();
    }

    @Override
    protected BiConsumer<LocalContextfulInstructionsCreator, ValueStore> branchCreator() {
        return regularBranchCreator();
    }

    @Override
    protected Function<ValueStore, ValueStore> elementValueStoreExtractor() {
        return shared ? e -> ((RemoteVariable)e).withProcessor(proc) : e -> e;
    }
}
