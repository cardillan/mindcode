package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.util.IntRange;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.TMP_VARIABLE;

@NullMarked
public class ExternalArray extends ElementsArrayStore {
    private final LogicVariable memory;

    private ExternalArray(SourcePosition sourcePosition, String name, LogicVariable memory, int startOffset,
            @Nullable ArrayStore masterArray, List<ValueStore> elements) {
        super(sourcePosition, name, ArrayType.EXTERNAL, startOffset, elements.size(), masterArray, elements, false);
        this.memory = memory;
    }

    static ExternalArray create(InstructionProcessor processor, SourcePosition sourcePosition, String name,
            LogicVariable memory, IntRange range, @Nullable ArrayStore masterArray) {
        List<ValueStore> elements = range.stream().mapToObj(index -> (ValueStore) new ExternalVariable(sourcePosition,
                memory,LogicNumber.create(index), processor.nextTemp())).toList();

        return new ExternalArray(sourcePosition, name, memory, range.min(), masterArray, elements);
    }

    public static ExternalArray create(InstructionProcessor processor, SourcePosition sourcePosition, String name,
            LogicVariable memory, IntRange range) {
        return create(processor, sourcePosition, name, memory, range, null);
    }

    public static ExternalArray createInvalid(AstIdentifier identifier, int size) {
        List<ValueStore> elements = Collections.nCopies(size, new ExternalVariable(identifier.sourcePosition(),
                LogicVariable.INVALID, LogicVariable.INVALID, LogicVariable.INVALID));

        return new ExternalArray(identifier.sourcePosition(), identifier.getName(), LogicVariable.INVALID, 0, null, elements);
    }

    @Override
    public boolean valid() {
        return true;
    }

    @Override
    public boolean optimizeElementAccess() {
        return false;
    }

    public LogicVariable getMemory() {
        return memory;
    }

    @Override
    public ArrayStore subarray(SourcePosition sourcePosition, IntRange range) {
        return new ExternalArray(sourcePosition, name, memory, startOffset + range.min(), getMasterArray(), range.sublist(elements));
    }

    @Override
    public ArrayStore offset(int offset) {
        return new ExternalArray(sourcePosition, name, memory, startOffset + offset, masterArray, elements);
    }

    @Override
    public ValueStore getElement(ContextfulInstructionCreator creator, int index) {
        return elements.get(index);
    }

    @Override
    public ValueStore getElement(ContextfulInstructionCreator creator, SourcePosition sourcePosition, ValueStore index,
            boolean safeAccess) {
        if (startOffset == 0) {
            LogicValue fixedIndex = creator.defensiveCopy(index, TMP_VARIABLE);
            return new ArrayElement(sourcePosition, logicArray, fixedIndex, creator.nextTemp(), safeAccess);
        } else {
            LogicVariable actualIndex = creator.nextTemp();
            creator.createOp(Operation.ADD, actualIndex, index.getValue(creator), startOffsetNumber);
            return new ArrayElement(sourcePosition, logicArray, actualIndex, creator.nextTemp(), safeAccess);
        }
    }

    @Override
    public ExternalArray withSourcePosition(SourcePosition sourcePosition) {
        return new ExternalArray(sourcePosition, name, memory, startOffset, masterArray, elements);
    }
}
