package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstExpression;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Consumer;

import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.TMP_VARIABLE;

@NullMarked
public class ExternalArray extends AbstractArrayStore {
    private final LogicVariable memory;
    private final int baseIndex;
    private final LogicNumber baseIndexNumber;

    public ExternalArray(SourcePosition sourcePosition, String name, LogicVariable memory, int baseIndex, List<ValueStore> elements) {
        super(sourcePosition, name, elements);
        this.memory = memory;
        this.baseIndex = baseIndex;
        this.baseIndexNumber = LogicNumber.create(baseIndex);
    }

    @Override
    public ArrayType getArrayType() {
        return ArrayType.EXTERNAL;
    }

    @Override
    public int getStartOffset() {
        return baseIndex;
    }

    @Override
    public ArrayStore subarray(SourcePosition sourcePosition, int start, int end) {
        return new ExternalArray(sourcePosition, name, memory, baseIndex + start, elements.subList(start, end));
    }

    @Override
    public ValueStore getElement(ContextfulInstructionCreator creator, AstExpression node, ValueStore index) {
        if (baseIndex == 0) {
            LogicValue fixedIndex = creator.defensiveCopy(index, TMP_VARIABLE);
            return new ExternalArrayElement(node, fixedIndex, creator.nextTemp());
        } else {
            LogicVariable actualIndex = creator.nextTemp();
            creator.createOp(Operation.ADD, actualIndex, index.getValue(creator), baseIndexNumber);
            return new ExternalArrayElement(node, actualIndex, creator.nextTemp());
        }
    }

    @Override
    public ExternalArray withSourcePosition(SourcePosition sourcePosition) {
        return new ExternalArray(sourcePosition, name, memory, baseIndex, elements);
    }

    private class ExternalArrayElement implements ValueStore {
        private final AstExpression node;
        private final LogicValue index;
        private final LogicVariable transferVariable;

        public ExternalArrayElement(AstExpression node, LogicValue index, LogicVariable transferVariable) {
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
            //assembler.createReadArr(transferVariable, logicArray, index, REGULAR_INTERNAL);
            creator.createRead(transferVariable, memory, index);
            return transferVariable;
        }

        @Override
        public void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
            creator.createRead(target, memory, index);
        }

        @Override
        public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
            creator.createWrite(value, memory, index);
        }

        @Override
        public SourcePosition sourcePosition() {
            return node.sourcePosition();
        }

        @Override
        public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
            valueSetter.accept(transferVariable);
            creator.createWrite(transferVariable, memory, index);
        }

        @Override
        public LogicValue getWriteVariable(ContextfulInstructionCreator creator) {
            return transferVariable;
        }

        @Override
        public void storeValue(ContextfulInstructionCreator creator) {
            creator.createWrite(transferVariable, memory, index);
        }
    }
}
