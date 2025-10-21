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
import info.teksol.mc.mindcode.logic.instructions.SideEffects;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public abstract class SharedArrayConstructor extends AbstractArrayConstructor {
    protected final LogicVariable arrayInd;
    protected final LogicVariable arrayRet;
    protected final LogicVariable arrayElem;

    public SharedArrayConstructor(ArrayConstructorContext context, ArrayAccessInstruction instruction, String indexSuffix,
            String returnSuffix, String elementSuffix) {
        super(context, instruction);

        NameCreator nameCreator = context.nameCreator();
        String baseName = arrayStore.getName();
        arrayInd = LogicVariable.arrayAccess(baseName, "*" + indexSuffix, nameCreator.arrayAccess(baseName, indexSuffix));
        arrayRet = LogicVariable.arrayReturn(baseName, "*" + returnSuffix, nameCreator.arrayAccess(baseName, returnSuffix));
        arrayElem = LogicVariable.arrayAccess(baseName, "*" + elementSuffix, nameCreator.arrayAccess(baseName, elementSuffix));
    }

    protected abstract SideEffects createCallSideEffects();

    @Override
    protected LogicValue transferVariable() {
        return arrayElem;
    }

    @Override
    public void generateJumpTable(Map<String, JumpTable> jumpTables) {
        if (skipCompactLookup()) return;

        String tableId = getJumpTableId();
        JumpTable jumpTable = jumpTables.get(tableId);
        if (jumpTable == null || useTextTables && !jumpTable.usesTextTable()) {
            jumpTables.put(tableId, buildJumpTable(tableId));
        }
    }

    private JumpTable buildJumpTable(String tableId) {
        boolean folded = folded();
        List<LogicInstruction> instructions = new ArrayList<>();

        AstContext astContext = rootAstContext.createSubcontext(AstContextType.JUMPS, AstSubcontextType.BASIC, 1.0);
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, instructions::add);

        creator.createEnd();
        LogicLabel startLabel = processor.nextLabel();
        LogicLabel firstLabel = profile.isSymbolicLabels() ? processor.nextLabel() : startLabel;
        LogicLabel marker = processor.nextMarker();

        if (profile.isSymbolicLabels()) {
            creator.createLabel(startLabel).setMarker(startLabel);
            LogicVariable jumpValue = folded ? processor.nextTemp() : arrayInd;
            if (folded) {
                LogicNumber modulo = LogicNumber.create(roundUpToEven(arrayStore.getSize()));
                creator.createOp(Operation.MOD, jumpValue, arrayInd, modulo);
            }
            creator.createMultiJump(firstLabel, jumpValue, LogicNumber.ZERO, marker);
        }

        Runnable createExit = () -> creator.createReturn(arrayRet);
        List<LogicLabel> branchLabels = new ArrayList<>();
        if (folded) {
            LogicNumber limit = useTextTables
                    ? LogicNumber.create((arrayStore.getSize() + 1) / 2)
                    : LogicNumber.create(roundUpToEven(arrayStore.getSize()));
            generateFoldedJumpTable(creator, firstLabel, marker,
                    arrayInd, limit, arrayElem, createExit, false, branchLabels);
        } else {
            generateJumpTable(creator, firstLabel, marker, createExit, false, branchLabels);
        }
        return new JumpTable(tableId, useTextTables, startLabel, marker, instructions, branchLabels);
    }

    @Override
    public void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, JumpTable> jumpTables) {
        JumpTable jumpTable = jumpTables.get(getJumpTableId());
        if (useTextTables) {
            expandInstructionTextTable(consumer, jumpTable);
        } else if (profile.isSymbolicLabels()) {
            expandInstructionSymbolicLabels(consumer, jumpTable);
        } else {
            expandInstructionDirectAddress(consumer, jumpTable);
        }
    }

    protected abstract void prepareTableCall(LocalContextfulInstructionsCreator creator);
    protected abstract void finishTableCall(LocalContextfulInstructionsCreator creator);

    private void expandInstructionSymbolicLabels(Consumer<LogicInstruction> consumer, JumpTable jumpTable) {
        AstContext astContext = instruction.getAstContext().createSubcontext(AstSubcontextType.ARRAY, 1.0);
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);

        if (!skipCompactLookup()) {
            prepareTableCall(creator);
            creator.createOp(Operation.MUL, arrayInd, instruction.getIndex(), LogicNumber.TWO);
            generateBoundsCheck(astContext, consumer, arrayInd, 2);
            creator.createCallStackless(jumpTable.label(), arrayRet, LogicVariable.INVALID).setSideEffects(createCallSideEffects());
        }

        finishTableCall(creator);
    }

    private void expandInstructionDirectAddress(Consumer<LogicInstruction> consumer, JumpTable jumpTable) {
        boolean folded = folded();
        AstContext astContext = instruction.getAstContext();
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);

        if (!skipCompactLookup()) {
            LogicLabel marker2 = processor.nextMarker();
            LogicLabel returnLabel = processor.nextLabel();

            prepareTableCall(creator);
            creator.createSetAddress(arrayRet, returnLabel).setHoistId(marker2);
            LogicVariable index = folded ? arrayInd : processor.nextTemp();
            creator.createOp(Operation.MUL, index, instruction.getIndex(), LogicNumber.TWO);
            generateBoundsCheck(astContext, consumer, index, 2);
            LogicVariable branch = folded ? creator.nextTemp() : index;
            if (folded) {
                LogicNumber modulo = LogicNumber.create(roundUpToEven(arrayStore.getSize()));
                creator.createOp(Operation.MOD, branch, index, modulo);
            }
            creator.createMultiCall(jumpTable.label(), branch, jumpTable.marker())
                    .setSideEffects(createCallSideEffects()).setHoistId(marker2);
            creator.createLabel(returnLabel);
        }

        finishTableCall(creator);
    }

    private void expandInstructionTextTable(Consumer<LogicInstruction> consumer, JumpTable jumpTable) {
        boolean folded = folded();
        AstContext astContext = instruction.getAstContext();
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);

        if (!skipCompactLookup()) {
            LogicLabel marker2 = processor.nextMarker();
            LogicLabel returnLabel = processor.nextLabel();
            prepareTableCall(creator);
            creator.createSetAddress(arrayRet, returnLabel).setHoistId(marker2);
            generateBoundsCheck(astContext, consumer, instruction.getIndex(), 1);
            if (folded) {
                creator.createSet(arrayInd, instruction.getIndex());
            }
            creator.createMultiCall(instruction.getIndex(), jumpTable.marker()).setSideEffects(createCallSideEffects())
                    .setHoistId(marker2).setJumpTable(jumpTable.branchLabels());
            creator.createLabel(returnLabel);
        }

        finishTableCall(creator);
    }
}
