package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstExpression;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.logic.arguments.LogicArray;
import info.teksol.mc.mindcode.logic.arguments.LogicString;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.TMP_VARIABLE;
import static info.teksol.mc.mindcode.logic.instructions.ArrayOrganization.INTERNAL_REGULAR;

@NullMarked
public class InternalArray extends AbstractArrayStore {
    private final boolean remote;
    private final int startOffset;
    private final LogicArray logicArray;

    private InternalArray(SourcePosition sourcePosition, String name, List<ValueStore> elements, boolean remote) {
        super(sourcePosition, name, elements);
        this.remote = remote;
        startOffset = 0;
        logicArray = LogicArray.create(this);
    }

    private InternalArray(SourcePosition sourcePosition, String name, int offset, List<ValueStore> elements, boolean remote) {
        super(sourcePosition, name, elements);
        this.remote = remote;
        startOffset = offset;
        logicArray = LogicArray.create(this, offset, offset + elements.size());
    }

    public static InternalArray create(NameCreator nameCreator, AstIdentifier identifier, int size, boolean isVolatile, @Nullable LogicVariable processor) {
        if (processor != null) {
            CodeAssembler assembler = MindcodeCompiler.getContext().assembler();
            return new InternalArray(identifier.sourcePosition(),
                    nameCreator.arrayBase(processor.getName(), identifier.getName()),
                    IntStream.range(0, size)
                            .mapToObj(index -> (ValueStore) new RemoteVariable(identifier.sourcePosition(), processor,
                                    processor.getName() + "." + identifier.getName() + "[" + index + "]",
                                    LogicString.create(nameCreator.remoteArrayElement(identifier.getName(), index)),
                                    assembler.nextTemp(), false, false)).toList(), true);
        } else {
            return new InternalArray(identifier.sourcePosition(),
                    nameCreator.arrayBase("", identifier.getName()),
                    IntStream.range(0, size)
                            .mapToObj(index -> (ValueStore) LogicVariable.arrayElement(identifier, index,
                                    nameCreator.arrayElement(identifier.getName(), index), isVolatile)).toList(), false);
        }
    }

    public static InternalArray createConst(NameCreator nameCreator, AstIdentifier identifier, int size, List<ValueStore> elements) {
        List<ValueStore> wrappedElements = elements.stream().map(InternalArray::constantWrap).toList();
        return new InternalArray(identifier.sourcePosition(),
                nameCreator.arrayBase("", identifier.getName()), wrappedElements, false);
    }

    public static InternalArray createInvalid(NameCreator nameCreator, AstIdentifier identifier, int size) {
        return new InternalArray(identifier.sourcePosition(),
                nameCreator.arrayBase("", identifier.getName()),
                IntStream.of(size).mapToObj(index -> (ValueStore) LogicVariable.INVALID).toList(), false);
    }

    public LogicArray getLogicArray() {
        return logicArray;
    }

    @Override
    public ArrayType getArrayType() {
        return remote ? ArrayType.REMOTE : ArrayType.INTERNAL;
    }

    @Override
    public int getStartOffset() {
        return startOffset;
    }

    @Override
    public ArrayStore subarray(SourcePosition sourcePosition, int start, int end) {
        return new InternalArray(sourcePosition, name, startOffset + start, elements.subList(start, end), remote);
    }

    @Override
    public ValueStore getElement(ContextfulInstructionCreator creator, AstExpression node, ValueStore index) {
        LogicValue fixedIndex = creator.defensiveCopy(index, TMP_VARIABLE);
        return new InternalArrayElement(node, fixedIndex, creator.nextTemp());
    }

    @Override
    public InternalArray withSourcePosition(SourcePosition sourcePosition) {
        return new InternalArray(sourcePosition, name, elements, remote);
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

            creator.createReadArr(transferVariable, logicArray, index, INTERNAL_REGULAR);
            return transferVariable;
        }

        @Override
        public void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
            if (startOffset != 0) {
                throw new MindcodeInternalError("Internal subarray random access is not supported");
            }
            creator.createReadArr(target, logicArray, index, INTERNAL_REGULAR);
        }

        @Override
        public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
            if (startOffset != 0) {
                throw new MindcodeInternalError("Internal subarray random access is not supported");
            }
            creator.createWriteArr(value, logicArray, index, INTERNAL_REGULAR);
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
            creator.createWriteArr(transferVariable, logicArray, index, INTERNAL_REGULAR);
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
            creator.createWriteArr(transferVariable, logicArray, index, INTERNAL_REGULAR);
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
