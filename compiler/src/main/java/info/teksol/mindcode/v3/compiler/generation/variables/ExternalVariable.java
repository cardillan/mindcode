package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.logic.LogicLiteral;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.generation.CodeBuilder;

import java.util.function.Consumer;

/// NodeValue representation of an external variable.
/// This implementation uses a single temporary variable (transferVariable) to transfer values from/to
/// external memory.
public class ExternalVariable implements NodeValue {
    private final LogicVariable memory;
    private final LogicLiteral index;
    private final LogicVariable transferVariable;

    public ExternalVariable(LogicVariable memory, LogicLiteral index, LogicVariable transferVariable) {
        this.memory = memory;
        this.index = index;
        this.transferVariable = transferVariable;
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public LogicValue getValue(CodeBuilder codeBuilder) {
        codeBuilder.addRead(transferVariable, memory, index);
        return transferVariable;
    }

    @Override
    public void setValue(CodeBuilder codeBuilder, LogicValue value) {
        codeBuilder.addWrite(value, memory, index);
    }

    @Override
    public void writeValue(CodeBuilder codeBuilder, Consumer<LogicVariable> valueSetter) {
        valueSetter.accept(transferVariable);
        codeBuilder.addWrite(transferVariable, memory, index);
    }
}
