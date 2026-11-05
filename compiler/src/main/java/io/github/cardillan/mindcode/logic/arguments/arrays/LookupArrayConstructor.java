package io.github.cardillan.mindcode.logic.arguments.arrays;

import io.github.cardillan.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mindcode.compiler.astcontext.AstContextType;
import io.github.cardillan.mindcode.compiler.astcontext.AstSubcontextType;
import io.github.cardillan.mindcode.compiler.generation.variables.ElementsArrayStore;
import io.github.cardillan.mindcode.compiler.generation.variables.NameCreator;
import io.github.cardillan.mindcode.compiler.generation.variables.ValueStore;
import io.github.cardillan.mindcode.compiler.postprocess.JumpTable;
import io.github.cardillan.mindcode.logic.arguments.LogicBuiltIn;
import io.github.cardillan.mindcode.logic.arguments.LogicKeyword;
import io.github.cardillan.mindcode.logic.arguments.LogicValue;
import io.github.cardillan.mindcode.logic.arguments.LogicVariable;
import io.github.cardillan.mindcode.logic.instructions.ArrayAccessInstruction;
import io.github.cardillan.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import io.github.cardillan.mindcode.logic.instructions.LogicInstruction;
import io.github.cardillan.mindcode.logic.instructions.SideEffects;
import io.github.cardillan.mindcode.logic.mimex.MindustryContent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public class LookupArrayConstructor extends TablelessArrayConstructor {
    private final LogicVariable arrayElem;

    public LookupArrayConstructor(ArrayConstructorContext context, ArrayAccessInstruction instruction) {
        super(context, instruction);

        NameCreator nameCreator = context.nameCreator();
        String baseName = arrayStore.getName();
        arrayElem = LogicVariable.arrayAccess(baseName, "*elem", nameCreator.arrayAccess(baseName, "elem"));

        instruction.setIndirectVariables(arrayElements());
    }

    @Override
    public int getInstructionSize(@Nullable Map<String, Integer> sharedStructures) {
        return boundsCheckSize() + offsetInstructions() + (instruction.isCompactAccessTarget() ? 1 : 3);
    }

    @Override
    public double getExecutionSteps() {
        return boundsCheckExecutionSteps() + offsetInstructions() + (instruction.isCompactAccessTarget() ? 1 : 3);
    }

    @Override
    public LogicVariable getElementNameVariable() {
        return arrayElem;
    }

    @Override
    public SideEffects createSideEffects() {
        return createNameBasedSideEffects();
    }

    @Override
    public void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, JumpTable> jumpTables) {
        generateBoundsCheck(instruction.getAstContext(), consumer, instruction.getIndex());
        LogicKeyword type = instruction.getArrayLookupType();

        AstContextType contextType = AstContextType.CODE;
        AstContext astContext = this.instruction.getAstContext().createChild(instruction.getAstContext().existingNode(),
                contextType, AstSubcontextType.BASIC);
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);
        LogicValue index = computeIndex(creator);
        creator.setSubcontextType(AstSubcontextType.BODY, 1.0);
        LogicVariable tmp0 = creator.nextTemp();

        if (!instruction.isCompactAccessTarget()) {
            creator.createLookup(type, tmp0, index).setNonNegativeInt(instruction.getIndex());
            creator.createSensor(arrayElem, tmp0, LogicBuiltIn.NAME);
        }

        LogicValue storageProcessor = arrayStore.isRemote() ? arrayStore.getProcessor() : LogicBuiltIn.THIS;
        createCompactAccessInstruction(creator, storageProcessor, arrayElem);

        Map<Integer, ? extends MindustryContent> lookupMap = processor.getMetadata().getLookupMap(type.getKeyword());
        assert lookupMap != null;

        if (arrayStore.getMasterArray() instanceof ElementsArrayStore elementsArrayStore) {
            List<ValueStore> elements = elementsArrayStore.getElements();
            for (int i = 0; i < elements.size(); i++) {
                elements.get(i).setArrayElementName(lookupMap.get(i).contentName());
            }
        }
    }
}
