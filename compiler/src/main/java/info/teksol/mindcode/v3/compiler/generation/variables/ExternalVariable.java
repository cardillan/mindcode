package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.generation.CodeBuilder;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

/// NodeValue representation of an external variable or a dynamic memory access.
/// This implementation uses a single temporary variable (transferVariable) to transfer values from/to
/// external memory.
@NullMarked
public class ExternalVariable implements NodeValue {
    private final LogicVariable memory;
    private final LogicValue index;
    private final LogicVariable transferVariable;

    public ExternalVariable(LogicVariable memory, LogicValue index, LogicVariable transferVariable) {
        this.memory = memory;
        this.index = index;
        this.transferVariable = transferVariable;
    }

    @Override
    public boolean isLvalue() {
        return true;
    }

    @Override
    public LogicValue getValue(CodeBuilder codeBuilder) {
        codeBuilder.createRead(transferVariable, memory, index);
        return transferVariable;
    }

    @Override
    public void setValue(CodeBuilder codeBuilder, LogicValue value) {
        codeBuilder.createWrite(value, memory, index);
    }

    @Override
    public void writeValue(CodeBuilder codeBuilder, Consumer<LogicVariable> valueSetter) {
        valueSetter.accept(transferVariable);
        codeBuilder.createWrite(transferVariable, memory, index);
    }
}