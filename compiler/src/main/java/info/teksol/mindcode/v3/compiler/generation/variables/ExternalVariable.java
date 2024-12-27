package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.generation.CodeAssembler;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

/// ValueStore representation of an external variable or a dynamic memory access.
/// This implementation uses a single temporary variable (transferVariable) to transfer values from/to
/// external memory.
@NullMarked
public class ExternalVariable implements ValueStore {
    private final LogicVariable memory;
    private final LogicValue index;
    private final LogicVariable transferVariable;

    public ExternalVariable(LogicVariable memory, LogicValue index, LogicVariable transferVariable) {
        this.memory = memory;
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
