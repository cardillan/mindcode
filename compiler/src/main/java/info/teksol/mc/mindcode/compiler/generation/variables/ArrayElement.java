package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.logic.arguments.LogicArray;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public class ArrayElement implements ValueStore {
    private final SourcePosition sourcePosition;
    private final LogicArray logicArray;
    private final LogicValue index;
    private final LogicVariable transferVariable;
    private final boolean safeAccess;

    public ArrayElement(SourcePosition sourcePosition, LogicArray logicArray, LogicValue index, LogicVariable transferVariable,
            boolean safeAccess) {
        this.sourcePosition = sourcePosition;
        this.logicArray = logicArray;
        this.index = index;
        this.transferVariable = transferVariable;
        this.safeAccess = safeAccess;
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public boolean isLvalue() {
        return logicArray.getArrayType() != ArrayType.CONSTANT;
    }

    @Override
    public LogicValue getValue(ContextfulInstructionCreator creator) {
        creator.createReadArr(transferVariable, logicArray, index).setSafeAccess(safeAccess);
        return transferVariable;
    }

    @Override
    public void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
        creator.createReadArr(target, logicArray, index).setSafeAccess(safeAccess);
    }

    @Override
    public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
        creator.createWriteArr(value, logicArray, index).setSafeAccess(safeAccess);
    }

    @Override
    public SourcePosition sourcePosition() {
        return sourcePosition;
    }

    @Override
    public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
        valueSetter.accept(transferVariable);
        creator.createWriteArr(transferVariable, logicArray, index).setSafeAccess(safeAccess);
    }

    @Override
    public LogicValue getWriteVariable(ContextfulInstructionCreator creator) {
        return transferVariable;
    }

    @Override
    public void storeValue(ContextfulInstructionCreator creator) {
        creator.createWriteArr(transferVariable, logicArray, index).setSafeAccess(safeAccess);
    }
}
