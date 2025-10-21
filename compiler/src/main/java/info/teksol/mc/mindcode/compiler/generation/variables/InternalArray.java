package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstExpression;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
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
    private final int startOffset;
    private final boolean declaredRemote;
    private final @Nullable LogicVariable processor;      // Actual processor in case of shared arrays
    private final LogicArray logicArray;

    private InternalArray(SourcePosition sourcePosition, LogicKeyword lookupType, String name,
            boolean declaredRemote, @Nullable LogicVariable processor, List<ValueStore> elements, ArrayType arrayType) {
        super(sourcePosition, name, elements);
        this.lookupType = lookupType;
        this.declaredRemote = declaredRemote;
        this.processor = processor;
        this.arrayType = arrayType;
        startOffset = 0;
        logicArray = LogicArray.create(this);
    }

    private InternalArray(SourcePosition sourcePosition, LogicKeyword lookupType, String name, boolean declaredRemote,
            @Nullable LogicVariable processor, int offset, List<ValueStore> elements, ArrayType arrayType) {
        super(sourcePosition, name, elements);
        this.lookupType = lookupType;
        this.processor = processor;
        this.declaredRemote = declaredRemote;
        this.arrayType = arrayType;
        startOffset = offset;
        logicArray = LogicArray.create(this, offset, offset + elements.size());
    }

    public static InternalArray create(InstructionProcessor instructionProcessor, ArrayNameCreator nameCreator,
            AstIdentifier identifier, int size, boolean isVolatile, boolean declaredRemote, @Nullable LogicVariable processor,
            boolean shared) {
        if (processor != null) {
            return new InternalArray(identifier.sourcePosition(), nameCreator.arrayLookupType(),
                    nameCreator.arrayBase(shared ? "" : processor.getName(), identifier.getName()),
                    declaredRemote, processor,
                    IntStream.range(0, size)
                            .mapToObj(index -> (ValueStore) new RemoteVariable(identifier.sourcePosition(), processor,
                                    processor.getName() + "." + identifier.getName() + "[" + index + "]",
                                    LogicString.create(nameCreator.remoteArrayElement(identifier.getName(), index)),
                                    instructionProcessor.nextTemp(), false, false, false)).toList(),
                    shared ? ArrayType.REMOTE_SHARED : ArrayType.REMOTE);
        } else {
            return new InternalArray(identifier.sourcePosition(), nameCreator.arrayLookupType(),
                    nameCreator.arrayBase("", identifier.getName()),
                    declaredRemote, null,
                    IntStream.range(0, size)
                            .mapToObj(index -> (ValueStore) LogicArrayElement.arrayElement(identifier, index,
                                    nameCreator.arrayElement(identifier.getName(), index), isVolatile)).toList(),
                    ArrayType.INTERNAL);
        }
    }

    public static InternalArray createConst(NameCreator nameCreator, AstIdentifier identifier, int size, List<ValueStore> elements) {
        List<ValueStore> wrappedElements = elements.stream().map(InternalArray::constantWrap).toList();
        return new InternalArray(identifier.sourcePosition(), LogicKeyword.INVALID,
                nameCreator.arrayBase("", identifier.getName()),
                false, null, wrappedElements, ArrayType.CONSTANT);
    }

    public static InternalArray createInvalid(NameCreator nameCreator, AstIdentifier identifier, int size) {
        return new InternalArray(identifier.sourcePosition(), LogicKeyword.INVALID,
                nameCreator.arrayBase("", identifier.getName()), false, null,
                IntStream.of(size).mapToObj(index -> (ValueStore) LogicVariable.INVALID).toList(), ArrayType.INTERNAL);
    }

    public LogicKeyword getLookupType() {
        return lookupType;
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
    public int getStartOffset() {
        return startOffset;
    }

    @Override
    public ArrayStore subarray(SourcePosition sourcePosition, int start, int end) {
        return new InternalArray(sourcePosition, lookupType, name, declaredRemote, processor, startOffset + start,
                elements.subList(start, end), arrayType);
    }

    public LogicVariable getProcessor() {
        return Objects.requireNonNull(processor);
    }

    @Override
    public ValueStore getElement(ContextfulInstructionCreator creator, AstExpression node, ValueStore index) {
        LogicValue fixedIndex = creator.defensiveCopy(index, TMP_VARIABLE);
        return new InternalArrayElement(node, fixedIndex, creator.nextTemp());
    }

    @Override
    public InternalArray withSourcePosition(SourcePosition sourcePosition) {
        return new InternalArray(sourcePosition, lookupType, name, declaredRemote, processor, elements, arrayType);
    }

    public class InternalArrayElement implements ValueStore {
        private final AstExpression node;
        private final LogicValue index;
        private final LogicVariable transferVariable;

        private InternalArrayElement(AstExpression node, LogicValue index, LogicVariable transferVariable) {
            this.node = node;
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
            if (startOffset != 0) {
                throw new MindcodeInternalError("Internal subarray random access is not supported");
            }

            creator.createReadArr(transferVariable, logicArray, index);
            return transferVariable;
        }

        @Override
        public void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
            if (startOffset != 0) {
                throw new MindcodeInternalError("Internal subarray random access is not supported");
            }
            creator.createReadArr(target, logicArray, index);
        }

        @Override
        public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
            if (startOffset != 0) {
                throw new MindcodeInternalError("Internal subarray random access is not supported");
            }
            creator.createWriteArr(value, logicArray, index);
        }

        @Override
        public SourcePosition sourcePosition() {
            return node.sourcePosition();
        }

        @Override
        public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
            if (startOffset != 0) {
                throw new MindcodeInternalError("Internal subarray random access is not supported");
            }
            valueSetter.accept(transferVariable);
            creator.createWriteArr(transferVariable, logicArray, index);
        }

        @Override
        public LogicValue getWriteVariable(ContextfulInstructionCreator creator) {
            return transferVariable;
        }

        @Override
        public void storeValue(ContextfulInstructionCreator creator) {
            if (startOffset != 0) {
                throw new MindcodeInternalError("Internal subarray random access is not supported");
            }
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
