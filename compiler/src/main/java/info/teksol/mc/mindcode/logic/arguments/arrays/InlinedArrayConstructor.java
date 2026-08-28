package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.compiler.postprocess.JumpTable;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
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

    protected abstract void finishTableCall(LocalContextfulInstructionsCreator creator);

    @Override
    public void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, JumpTable> jumpTables) {
        if (!skipCompactLookup()) {
            AstContext astContext = instruction.getAstContext().createSubcontext(AstSubcontextType.ARRAY, 1.0);
            LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);

            LogicLabel finalLabel = processor.nextLabel();
            LogicLabel firstLabel = processor.nextLabel();
            LogicLabel marker = processor.nextMarker();
            LogicValue index = computeIndex(creator);
            LogicVariable tmp;
            if (useTextTables) {
                generateBoundsCheck(astContext, consumer, instruction.getIndex(), 1);
                tmp = LogicVariable.INVALID;  // Won't be used
            } else if (folded()) {
                LogicVariable tmp1 = creator.nextTemp();
                generateBoundsCheck(astContext, consumer, instruction.getIndex(), 1);
                creator.createOp(Operation.SHL, tmp1, index, LogicNumber.ONE).setNonNegativeInt(index);
                LogicVariable tmp2 = creator.nextTemp();
                LogicNumber modulo = LogicNumber.create(roundUpToEven(arrayStore.getFullSize()));
                creator.createOp(Operation.MOD, tmp2, tmp1, modulo);
                tmp = tmp2;
            } else {
                tmp = creator.nextTemp();
                generateBoundsCheck(astContext, consumer, instruction.getIndex(), 1);
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
                LogicNumber limit = LogicNumber.create((arrayStore.getFullSize() + 1) / 2);
                generateFoldedJumpTable(creator, firstLabel, marker, index, limit, arrayElem,
                        createExit, true, branchLabels, ix -> ix.setNonNegativeIntTable(index));
            } else {
                generateJumpTable(creator, firstLabel, marker, createExit, true, branchLabels);
            }
            creator.createLabel(finalLabel);
            creator.popContext();
        }

        LocalContextfulInstructionsCreator creator2 = new LocalContextfulInstructionsCreator(processor, instruction.getAstContext(), consumer);
        finishTableCall(creator2);
    }

    protected final int inlinedTableSize() {
        return (folded() ? roundUpToEven(arrayStore.getFullSize()) : 2 * arrayStore.getFullSize()) - 1;
    }

    protected final double inlinedTableStepsSavings() {
        // The last jump in an inlined jump table is eliminated
        // Hit twice as often for even-sized folded arrays, or with text tables
        boolean even = arrayStore.getFullSize() % 2 == 0;
        return (folded() && (even || useTextTables) ? 2.0 : 1.0) / arrayStore.getFullSize();
    }
}
