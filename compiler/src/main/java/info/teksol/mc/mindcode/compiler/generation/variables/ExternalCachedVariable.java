package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
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
    public LogicValue getValue(CodeAssembler assembler) {
        return transferVariable;
    }

    @Override
    public void readValue(CodeAssembler assembler, LogicVariable target) {
        assembler.createSet(target, transferVariable);
    }

    @Override
    public void initialize(CodeAssembler assembler) {
        assembler.createRead(transferVariable, memory, index);
    }

    @Override
    public void setValue(CodeAssembler assembler, LogicValue value) {
        transferVariable.setValue(assembler, value);
        assembler.createWrite(transferVariable, memory, index);
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
