package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

/// ValueStore representation of an external variable or a dynamic memory access.
/// This implementation uses a single temporary variable (transferVariable) to transfer values from/to
/// external memory.
@NullMarked
public class ExternalCachedVariable implements ValueStore {
    private final SourcePosition sourcePosition;
    private final LogicVariable memory;
    private final LogicValue index;
    private final LogicVariable transferVariable;

    public ExternalCachedVariable(SourcePosition sourcePosition, LogicVariable memory, LogicValue index,
            LogicVariable transferVariable) {
        this.sourcePosition = sourcePosition;
        this.memory = memory;
        this.index = index;
        this.transferVariable = transferVariable;
    }

    @Override
    public SourcePosition sourcePosition() {
        return sourcePosition;
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
        return transferVariable;
    }

    @Override
    public void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
        creator.createSet(target, transferVariable);
    }

    @Override
    public void initialize(ContextfulInstructionCreator creator) {
        creator.createRead(transferVariable, memory, index);
    }

    @Override
    public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
        transferVariable.setValue(creator, value);
        creator.createWrite(transferVariable, memory, index);
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
