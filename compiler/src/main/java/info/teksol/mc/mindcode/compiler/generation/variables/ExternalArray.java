package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstArrayAccess;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Consumer;

import static info.teksol.mc.mindcode.logic.arguments.ArgumentType.TMP_VARIABLE;

@NullMarked
public class ExternalArray extends ArrayStore<ExternalVariable> {
    private final LogicVariable memory;
    private final int baseIndex;
    private final LogicNumber baseIndexNumber;

    ExternalArray(SourcePosition sourcePosition, String name, LogicVariable memory, int baseIndex, List<ExternalVariable> elements) {
        super(sourcePosition, name, elements);
        this.memory = memory;
        this.baseIndex = baseIndex;
        this.baseIndexNumber = LogicNumber.create(baseIndex);
    }

    @Override
    public ValueStore getElement(CodeAssembler assembler, AstArrayAccess node, ValueStore index) {
        if (baseIndex == 0) {
            LogicValue fixedIndex = assembler.defensiveCopy(index, TMP_VARIABLE);
            return new ExternalArrayElement(node, fixedIndex, assembler.nextTemp());
        } else {
            LogicVariable actualIndex = assembler.nextTemp();
            assembler.createOp(Operation.ADD, actualIndex, index.getValue(assembler), baseIndexNumber);
            return new ExternalArrayElement(node, actualIndex, assembler.nextTemp());
        }
    }

    @Override
    public ExternalArray withSourcePosition(SourcePosition sourcePosition) {
        return new ExternalArray(sourcePosition, name, memory, baseIndex, elements);
    }

    private class ExternalArrayElement implements ValueStore {
        private final AstArrayAccess node;
        private final LogicValue index;
        private final LogicVariable transferVariable;

        public ExternalArrayElement(AstArrayAccess node, LogicValue index, LogicVariable transferVariable) {
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
            assembler.createRead(transferVariable, memory, index);
            return transferVariable;
        }

        @Override
        public void setValue(CodeAssembler assembler, LogicValue value) {
            assembler.createWrite(value, memory, index);
        }

        @Override
        public SourcePosition sourcePosition() {
            return node.sourcePosition();
        }

        @Override
        public void writeValue(CodeAssembler assembler, Consumer<LogicVariable> valueSetter) {
            valueSetter.accept(transferVariable);
            assembler.createWrite(transferVariable, memory, index);
        }

        @Override
        public LogicValue getWriteVariable(CodeAssembler assembler) {
            return transferVariable;
        }

        @Override
        public void storeValue(CodeAssembler assembler) {
            assembler.createWrite(transferVariable, memory, index);
        }
    }
}
