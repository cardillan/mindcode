package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.compiler.postprocess.JumpTable;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.arguments.Operation;
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
            LogicVariable tmp;
            if (useTextTables) {
                generateBoundsCheck(astContext, consumer, instruction.getIndex(), 1);
                tmp = LogicVariable.INVALID;  // Won't be used
            } else if (folded()) {
                LogicVariable tmp1 = creator.nextTemp();
                creator.createOp(Operation.MUL, tmp1, instruction.getIndex(), LogicNumber.TWO);
                generateBoundsCheck(astContext, consumer, tmp1, 2);
                LogicVariable tmp2 = creator.nextTemp();
                LogicNumber modulo = LogicNumber.create(roundUpToEven(arrayStore.getSize()));
                creator.createOp(Operation.MOD, tmp2, tmp1, modulo);
                tmp = tmp2;
            } else {
                tmp = creator.nextTemp();
                creator.createOp(Operation.MUL, tmp, instruction.getIndex(), LogicNumber.TWO);
                generateBoundsCheck(astContext, consumer, tmp, 2);
            }

            creator.pushContext(AstContextType.JUMPS, AstSubcontextType.BASIC);
            creator.setSubcontextType(AstSubcontextType.ARRAY, 1.0);

            List<LogicLabel> branchLabels = new ArrayList<>();
            if (useTextTables) {
                creator.createMultiJump(instruction.getIndex(), marker).setSideEffects(createSideEffects()).setJumpTable(branchLabels);
            } else {
                creator.createMultiJump(firstLabel, tmp, LogicNumber.ZERO, marker).setSideEffects(createSideEffects());
            }

            Runnable createExit = () -> creator.createJumpUnconditional(finalLabel);
            if (folded()) {
                LogicNumber limit = LogicNumber.create((arrayStore.getSize() + 1) / 2);
                generateFoldedJumpTable(creator, firstLabel, marker, instruction.getIndex(), limit, arrayElem,
                        createExit, true, branchLabels);
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
        return (folded() ? roundUpToEven(arrayStore.getSize()) : 2 * arrayStore.getSize()) - 1;
    }

    protected final double inlinedTableStepsSavings() {
        // The last jump in an inlined jump table is eliminated
        // Hit twice as often for even-sized folded arrays
        return (folded() && arrayStore.getSize() % 2 == 0 ? 2.0 : 1.0) / arrayStore.getSize();
    }
}
