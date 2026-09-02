package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.TMP_VARIABLE;

@NullMarked
public class InternalArray extends AbstractArrayStore {
    private final LogicKeyword lookupType;
    private final ArrayType arrayType;
    private final LogicValue arrayOffset;
    private final boolean declaredRemote;
    private final @Nullable MindcodeFunction function;
    private final @Nullable LogicVariable processor;      // Actual processor in case of shared arrays
    private final LogicArray logicArray;

    private InternalArray(SourcePosition sourcePosition, LogicKeyword lookupType, String name, boolean declaredRemote,
            @Nullable MindcodeFunction function, @Nullable LogicVariable processor, LogicValue arrayOffset, int startOffset, int size,
            List<ValueStore> elements, @Nullable ArrayStore masterArray, ArrayType arrayType, boolean subarray) {
        super(sourcePosition, name, startOffset, size, masterArray, elements);
        this.lookupType = lookupType;
        this.processor = processor;
        this.function = function;
        this.declaredRemote = declaredRemote;
        this.arrayType = arrayType;
        this.arrayOffset = arrayOffset;
        logicArray = subarray ? LogicArray.create(this, startOffset, startOffset + elements.size()) : LogicArray.create(this);
    }

    public static InternalArray create(InstructionProcessor instructionProcessor, ArrayNameCreator nameCreator,
            @Nullable MindcodeFunction function, AstIdentifier identifier, int variableIndex, int size,
            boolean isVolatile, boolean declaredRemote, @Nullable LogicVariable processor, boolean shared) {
        if (processor != null) {
            if (function != null && function.isRecursive()) {
                throw new MindcodeInternalError("Recursive functions can't have remote arrays.");
            }

            return new InternalArray(identifier.sourcePosition(), nameCreator.arrayLookupType(),
                    nameCreator.arrayBase(null, shared ? "" : processor.getName(), identifier.getName(), variableIndex),
                    declaredRemote, function, processor, LogicNumber.ZERO, 0, size,
                    IntStream.range(0, size)
                            .mapToObj(index -> (ValueStore) new RemoteVariable(identifier.sourcePosition(), processor,
                                    processor.getName() + "." + identifier.getName() + "[" + index + "]",
                                    LogicString.create(nameCreator.remoteArrayElement(identifier.getName(), index)),
                                    instructionProcessor.nextTemp(), false, false, false)).toList(),
                    null, shared ? ArrayType.REMOTE_SHARED : ArrayType.REMOTE, false);
        } else {
            // Compute the stack depth for recursive functions
            int stackDepth = function != null && function.isRecursive() ? function.getProfile().getStackDepth() : 1;
            LogicValue arrayOffset = function != null && function.isRecursive()
                    ? LogicVariable.fnArrayOffset(function, identifier, nameCreator.arrayOffset(function, identifier.getName(), variableIndex))
                    : LogicNumber.ZERO;

            InternalArray array = new InternalArray(identifier.sourcePosition(), nameCreator.arrayLookupType(),
                    nameCreator.arrayBase(function, "", identifier.getName(), variableIndex),
                    declaredRemote, function, null, arrayOffset, 0, size,
                    IntStream.range(0, size * stackDepth)
                            .mapToObj(index -> (ValueStore) LogicArrayElement.arrayElement(identifier, index,
                                    nameCreator.arrayElement(function, identifier.getName(), variableIndex, index), isVolatile)).toList(),
                    null, ArrayType.INTERNAL, false);

            if (function != null) {
                function.addArray(array);
            }

            return array;
        }
    }

    public static InternalArray createConst(NameCreator nameCreator, @Nullable MindcodeFunction function, AstIdentifier identifier, List<ValueStore> elements) {
        List<ValueStore> wrappedElements = elements.stream().map(InternalArray::constantWrap).toList();
        return new InternalArray(identifier.sourcePosition(), LogicKeyword.INVALID,
                nameCreator.arrayBase(function, "", identifier.getName(), 0),
                false, null, null, LogicNumber.ZERO, 0, elements.size(),
                wrappedElements, null, ArrayType.CONSTANT, false);
    }

    public static InternalArray createInvalid(NameCreator nameCreator, AstIdentifier identifier, int size) {
        return new InternalArray(identifier.sourcePosition(), LogicKeyword.INVALID,
                nameCreator.arrayBase(null, "", identifier.getName(), 0),
                false, null, null, LogicNumber.ZERO, 0, size,
                IntStream.of(size).mapToObj(index -> (ValueStore) LogicVariable.INVALID).toList(),
                null, ArrayType.INTERNAL, false);
    }

    public LogicKeyword getLookupType() {
        return lookupType;
    }

    @Override
    public boolean valid() {
        return elements.getFirst() != LogicVariable.INVALID;
    }

    @Override
    public boolean isDeclaredRemote() {
        return declaredRemote;
    }

    @Override
    public ArrayType getArrayType() {
        return arrayType;
    }

    @Override
    public LogicArray getLogicArray() {
        return logicArray;
    }

    @Override
    public @Nullable MindcodeFunction getFunction() {
        return function;
    }

    @Override
    public LogicValue getArrayOffset() {
        return arrayOffset;
    }

    @Override
    public boolean optimizeElementAccess() {
        return !hasArrayOffset();
    }

    @Override
    public ArrayStore subarray(SourcePosition sourcePosition, int start, int end) {
        return new InternalArray(sourcePosition, lookupType, name, declaredRemote, function, processor, arrayOffset, startOffset + start,
                end - start, elements.subList(start, end), getMasterArray(), arrayType, true);
    }

    @Override
    public ArrayStore offset(int offset) {
        return new InternalArray(sourcePosition, lookupType, name, declaredRemote, function, processor, arrayOffset, startOffset + offset,
                size, elements, getMasterArray(), arrayType, getMasterArray().getSize() != getSize());
    }

    @Override
    public ArrayStore nonrecursive() {
        return hasArrayOffset()
                ? new InternalArray(sourcePosition, lookupType, name, declaredRemote, function, processor, LogicNumber.ZERO, startOffset,
                        size, elements.subList(0, size), getMasterArray(), arrayType, getMasterArray().getSize() != getSize())
                : this;
    }

    public LogicVariable getProcessor() {
        return Objects.requireNonNull(processor);
    }

    @Override
    public ValueStore getElement(ContextfulInstructionCreator creator, int index) {
        if (hasArrayOffset()) {
            return new InternalArrayElement(sourcePosition, LogicNumber.create(index), creator.nextTemp());
        } else {
            return elements.get(index);
        }
    }

    @Override
    public ValueStore getElement(ContextfulInstructionCreator creator, SourcePosition sourcePosition, ValueStore index) {
        LogicValue fixedIndex = creator.defensiveCopy(index, TMP_VARIABLE);
        return new InternalArrayElement(sourcePosition, fixedIndex, creator.nextTemp());
    }

    @Override
    public InternalArray withSourcePosition(SourcePosition sourcePosition) {
        return new InternalArray(sourcePosition, lookupType, name, declaredRemote, function, processor, arrayOffset,
                startOffset, size, elements, masterArray, arrayType, getMasterArray().getSize() != getSize());
    }

    public class InternalArrayElement implements ValueStore {
        private final SourcePosition sourcePosition;
        private final LogicValue index;
        private final LogicVariable transferVariable;

        private InternalArrayElement(SourcePosition sourcePosition, LogicValue index, LogicVariable transferVariable) {
            this.sourcePosition = sourcePosition;
            this.index = index;
            this.transferVariable = transferVariable;
        }

        @Override
        public boolean isComplex() {
            return true;
        }

        @Override
        public boolean isLvalue() {
            return true;
        }

        @Override
        public LogicValue getValue(ContextfulInstructionCreator creator) {
            creator.createReadArr(transferVariable, logicArray, index);
            return transferVariable;
        }

        @Override
        public void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
            creator.createReadArr(target, logicArray, index);
        }

        @Override
        public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
            creator.createWriteArr(value, logicArray, index);
        }

        @Override
        public SourcePosition sourcePosition() {
            return sourcePosition;
        }

        @Override
        public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
            valueSetter.accept(transferVariable);
            creator.createWriteArr(transferVariable, logicArray, index);
        }

        @Override
        public LogicValue getWriteVariable(ContextfulInstructionCreator creator) {
            return transferVariable;
        }

        @Override
        public void storeValue(ContextfulInstructionCreator creator) {
            creator.createWriteArr(transferVariable, logicArray, index);
        }
    }

    private static ValueStore constantWrap(ValueStore valueStore) {
        return new ConstantArrayElement(valueStore);
    }

    public static class ConstantArrayElement implements ValueStore {
        private final ValueStore valueStore;

        public ConstantArrayElement(ValueStore valueStore) {
            this.valueStore = valueStore;
        }

        @Override
        public ValueStore unwrap() {
            return valueStore.unwrap();
        }

        @Override
        public boolean isComplex() {
            return valueStore.isComplex();
        }

        @Override
        public boolean isLvalue() {
            return false;
        }

        @Override
        public LogicValue getValue(ContextfulInstructionCreator creator) {
            return valueStore.getValue(creator);
        }

        @Override
        public void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
            valueStore.readValue(creator, target);
        }

        @Override
        public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
            throw new MindcodeInternalError("Writes to constant array element are not supported");
        }

        @Override
        public SourcePosition sourcePosition() {
            return valueStore.sourcePosition();
        }

        @Override
        public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
            throw new MindcodeInternalError("Writes to constant array element are not supported");
        }

        @Override
        public LogicValue getWriteVariable(ContextfulInstructionCreator creator) {
            throw new MindcodeInternalError("Writes to constant array element are not supported");
        }

        @Override
        public void storeValue(ContextfulInstructionCreator creator) {
            throw new MindcodeInternalError("Writes to constant array element are not supported");
        }
    }
}
