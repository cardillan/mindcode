package io.github.cardillan.mindcode.logic.arguments.arrays;

import io.github.cardillan.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mindcode.compiler.astcontext.AstContextType;
import io.github.cardillan.mindcode.compiler.astcontext.AstSubcontextType;
import io.github.cardillan.mindcode.compiler.generation.variables.NameCreator;
import io.github.cardillan.mindcode.compiler.generation.variables.ValueStore;
import io.github.cardillan.mindcode.compiler.postprocess.JumpTable;
import io.github.cardillan.mindcode.logic.arguments.*;
import io.github.cardillan.mindcode.logic.instructions.ArrayAccessInstruction;
import io.github.cardillan.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import io.github.cardillan.mindcode.logic.instructions.LogicInstruction;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public abstract class InlinedArrayConstructor extends AbstractArrayConstructor {
    protected final LogicVariable arrayElem;

    public InlinedArrayConstructor(ArrayConstructorContext context, ArrayAccessInstruction instruction, LogicVariable arrayElem) {
        super(context, instruction);
        this.arrayElem = arrayElem;
    }

    public InlinedArrayConstructor(ArrayConstructorContext context, ArrayAccessInstruction instruction, String elementSuffix) {
        super(context, instruction);
        NameCreator nameCreator = context.nameCreator();
        String baseName = arrayStore.getName();
        arrayElem = LogicVariable.arrayAccess(baseName, "*" + elementSuffix, nameCreator.arrayAccess(baseName, elementSuffix));
    }

    @Override
    protected int getStartOffset() {
        // Inlined jump table is built for the subarray, start offset is 0
        return 0;
    }

    protected abstract void finishTableCall(LocalContextfulInstructionsCreator creator);

    @Override
    public void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, JumpTable> jumpTables) {
        if (!skipCompactLookup()) {
            AstContext astContext = instruction.getAstContext().createSubcontext(AstSubcontextType.ARRAY, 1.0);
            generateBoundsCheck(astContext, consumer, instruction.getIndex());

            LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);

            LogicLabel finalLabel = processor.nextLabel();
            LogicLabel firstLabel = processor.nextLabel();
            LogicLabel marker = processor.nextMarker();
            LogicValue index = computeIndex(creator);
            List<ValueStore> elements = arrayStore.getElements();

            LogicVariable tmp;
            if (useTextTables) {
                tmp = LogicVariable.INVALID;  // Won't be used
            } else if (folded()) {
                LogicVariable tmp1 = creator.nextTemp();
                creator.createOp(Operation.SHL, tmp1, index, LogicNumber.ONE).setNonNegativeInt(index);
                LogicVariable tmp2 = creator.nextTemp();
                LogicNumber modulo = LogicNumber.create(roundUpToEven(elements.size()));
                creator.createOp(Operation.MOD, tmp2, tmp1, modulo);
                tmp = tmp2;
            } else {
                tmp = creator.nextTemp();
                creator.createOp(Operation.SHL, tmp, index, LogicNumber.ONE).setNonNegativeInt(index);
            }

            creator.pushContext(AstContextType.JUMPS, AstSubcontextType.BASIC);
            creator.setSubcontextType(AstSubcontextType.ARRAY, 1.0);

            List<LogicLabel> branchLabels = new ArrayList<>();
            if (useTextTables) {
                creator.createMultiJump(index, marker).setJumpTable(branchLabels)
                        .setSideEffects(createSideEffects()).setNonNegativeInt(index);
            } else {
                creator.createMultiJump(firstLabel, tmp, LogicNumber.ZERO, marker).setSideEffects(createSideEffects());
            }

            Runnable createExit = () -> creator.createJumpUnconditional(finalLabel);
            if (folded()) {
                LogicNumber limit = LogicNumber.create((elements.size() + 1) / 2);
                generateFoldedJumpTable(creator, elements, firstLabel, marker, index, limit, arrayElem,
                        createExit, true, branchLabels, ix -> ix.setNonNegativeIntTable(index));
            } else {
                generateJumpTable(creator, elements, firstLabel, marker, createExit, true, branchLabels);
            }
            creator.createLabel(finalLabel);
            creator.popContext();
        }

        LocalContextfulInstructionsCreator creator2 = new LocalContextfulInstructionsCreator(processor, instruction.getAstContext(), consumer);
        finishTableCall(creator2);
    }

    protected final int inlinedTableSize() {
        return (folded() ? roundUpToEven(arrayStore.fullSize()) : 2 * arrayStore.fullSize()) - 1;
    }

    protected final double inlinedTableStepsSavings() {
        // The last jump in an inlined jump table is eliminated
        // Hit twice as often for even-sized folded arrays, or with text tables
        boolean even = arrayStore.fullSize() % 2 == 0;
        return (folded() && (even || useTextTables) ? 2.0 : 1.0) / arrayStore.fullSize();
    }
}
