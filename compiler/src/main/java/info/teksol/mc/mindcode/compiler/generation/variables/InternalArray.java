package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstExpression;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.logic.arguments.LogicArray;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.TMP_VARIABLE;

@NullMarked
public class InternalArray extends ArrayStore<LogicVariable> {
    private final LogicArray logicArray;

    private InternalArray(SourcePosition sourcePosition, String name, List<LogicVariable> elements) {
        super(sourcePosition, name, elements);
        logicArray = LogicArray.create(this);
    }

    public static InternalArray create(AstIdentifier identifier, int size) {
        return new InternalArray(identifier.sourcePosition(), identifier.getName(), IntStream.range(0, size)
                .mapToObj(index -> LogicVariable.arrayElement(identifier, index)).toList());
    }

    public static InternalArray createInvalid(AstIdentifier identifier, int size) {
        return new InternalArray(identifier.sourcePosition(), identifier.getName(), IntStream.of(size)
                .mapToObj(index -> LogicVariable.INVALID).toList());
    }

    @Override
    public ValueStore getElement(CodeAssembler assembler, AstExpression node, ValueStore index) {
        LogicValue fixedIndex = assembler.defensiveCopy(index, TMP_VARIABLE);
        return new InternalArrayElement(node, fixedIndex, assembler.nextTemp());
    }

    @Override
    public InternalArray withSourcePosition(SourcePosition sourcePosition) {
        return new InternalArray(sourcePosition, name, elements);
    }

    private class InternalArrayElement implements ValueStore {
        private final AstExpression node;
        private final LogicValue index;
        private final LogicVariable transferVariable;

        public InternalArrayElement(AstExpression node, LogicValue index, LogicVariable transferVariable) {
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
        public LogicValue getValue(CodeAssembler assembler) {
            assembler.createReadArr(transferVariable, logicArray, index);
            return transferVariable;
        }

        @Override
        public void readValue(CodeAssembler assembler, LogicVariable target) {
            assembler.createReadArr(target, logicArray, index);
        }

        @Override
        public void setValue(CodeAssembler assembler, LogicValue value) {
            assembler.createWriteArr(value, logicArray, index);
        }

        @Override
        public SourcePosition sourcePosition() {
            return node.sourcePosition();
        }

        @Override
        public void writeValue(CodeAssembler assembler, Consumer<LogicVariable> valueSetter) {
            valueSetter.accept(transferVariable);
            assembler.createWriteArr(transferVariable, logicArray, index);
        }

        @Override
        public LogicValue getWriteVariable(CodeAssembler assembler) {
            return transferVariable;
        }

        @Override
        public void storeValue(CodeAssembler assembler) {
            assembler.createWriteArr(transferVariable, logicArray, index);
        }
    }
}
