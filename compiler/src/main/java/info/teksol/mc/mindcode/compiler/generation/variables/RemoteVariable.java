package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

/// ValueStore representation of a remote variable.
/// This implementation uses a single temporary variable (transferVariable) to transfer values from/to
/// the remote processor.
@NullMarked
public class RemoteVariable implements FunctionParameter {
    private final SourcePosition sourcePosition;
    private final LogicVariable processor;
    private final LogicValue variableName;
    private final LogicVariable transferVariable;
    private final boolean input;
    private final boolean output;

    public RemoteVariable(SourcePosition sourcePosition, LogicVariable processor, LogicValue variableName, LogicVariable transferVariable,
            boolean input, boolean output) {
        this.sourcePosition = sourcePosition;
        this.processor = processor;
        this.variableName = variableName;
        this.transferVariable = transferVariable;
        this.input = input;
        this.output = output;
    }

    public LogicValue getVariableName() {
        return variableName;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public boolean isInput() {
        return input;
    }

    @Override
    public boolean isOutput() {
        return output;
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
        assembler.createRead(transferVariable, processor, variableName);
        return transferVariable;
    }

    @Override
    public void readValue(CodeAssembler assembler, LogicVariable target) {
        assembler.createRead(target, processor, variableName);
    }

    @Override
    public void setValue(CodeAssembler assembler, LogicValue value) {
        assembler.createWrite(value, processor, variableName);
    }

    @Override
    public void writeValue(CodeAssembler assembler, Consumer<LogicVariable> valueSetter) {
        valueSetter.accept(transferVariable);
        assembler.createWrite(transferVariable, processor, variableName);
    }

    @Override
    public LogicValue getWriteVariable(CodeAssembler assembler) {
        return transferVariable;
    }

    @Override
    public void storeValue(CodeAssembler assembler) {
        assembler.createWrite(transferVariable, processor, variableName);
    }
}
