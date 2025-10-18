package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.compiler.postprocess.JumpTable;
import info.teksol.mc.mindcode.logic.arguments.LogicBuiltIn;
import info.teksol.mc.mindcode.logic.arguments.LogicKeyword;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.SideEffects;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public class LookupArrayConstructor extends TablelessArrayConstructor {
    private final LogicVariable arrayElem;

    public LookupArrayConstructor(ArrayAccessInstruction instruction) {
        super(instruction);

        NameCreator nameCreator = MindcodeCompiler.getContext().nameCreator();
        String baseName = arrayStore.getName();
        arrayElem = LogicVariable.arrayAccess(baseName, "*elem", nameCreator.arrayAccess(baseName, "elem"));

        instruction.setIndirectVariables(arrayElements());
    }

    @Override
    public int getInstructionSize(@Nullable Map<String, Integer> sharedStructures) {
        return profile.getBoundaryChecks().getSize() + (instruction.isCompactAccessTarget() ? 1 : 3);
    }

    @Override
    public double getExecutionSteps() {
        return profile.getBoundaryChecks().getExecutionSteps() + (instruction.isCompactAccessTarget() ? 1 : 3);
    }

    @Override
    public LogicVariable getElementNameVariable() {
        return arrayElem;
    }

    @Override
    public SideEffects createSideEffects() {
        return switch (accessType) {
            case READ -> SideEffects.of(arrayElements(), List.of(arrayElem), List.of());
            case WRITE -> SideEffects.of(List.of(), List.of(arrayElem), arrayElements());
        };
    }

    @Override
    public void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, JumpTable> jumpTables) {
        generateBoundsCheck(instruction.getAstContext(), consumer, instruction.getIndex(), 1 );
        LogicKeyword type = instruction.getArrayLookupType();

        AstContextType contextType = AstContextType.BODY;
        AstContext astContext = this.instruction.getAstContext().createChild(instruction.getAstContext().existingNode(),
                contextType, AstSubcontextType.BASIC);
        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);
        creator.setSubcontextType(AstSubcontextType.BODY, 1.0);
        LogicVariable tmp0 = creator.nextTemp();

        if (!instruction.isCompactAccessTarget()) {
            creator.createLookup(type, tmp0, instruction.getIndex());
            creator.createSensor(arrayElem, tmp0, LogicBuiltIn.NAME);
        }

        LogicValue storageProcessor = arrayStore.isRemote() ? arrayStore.getProcessor() : LogicBuiltIn.THIS;
        createCompactAccessInstruction(creator, storageProcessor, arrayElem);

        List<ValueStore> elements = arrayStore.getElements();
        Map<Integer, ? extends MindustryContent> lookupMap = processor.getMetadata().getLookupMap(type.getKeyword());
        assert lookupMap != null;

        for (int i = 0; i < elements.size(); i++) {
            elements.get(i).setArrayElementName(lookupMap.get(i).contentName());
        }
    }
}
