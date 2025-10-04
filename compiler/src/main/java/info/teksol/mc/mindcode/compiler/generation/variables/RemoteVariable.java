package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.logic.arguments.LogicString;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

/// ValueStore representation of a remote variable.
/// This implementation uses a single temporary variable (transferVariable) to transfer values from/to
/// the remote processor.
@NullMarked
public class RemoteVariable implements FunctionParameter {
    private final SourcePosition sourcePosition;
    private final LogicVariable processor;
    private final String name;
    private final LogicString remoteName;
    private final LogicVariable transferVariable;
    private final boolean input;
    private final boolean output;

    public RemoteVariable(SourcePosition sourcePosition, LogicVariable processor, String name, LogicString remoteName, LogicVariable transferVariable,
            boolean input, boolean output) {
        this.sourcePosition = sourcePosition;
        this.processor = processor;
        this.name = name;
        this.remoteName = remoteName;
        this.transferVariable = transferVariable;
        this.input = input;
        this.output = output;
    }

    public LogicVariable getProcessor() {
        return processor;
    }

    public LogicValue getRemoteName() {
        return remoteName;
    }

    @Override
    public String getName() {
        return name;
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
    public LogicString getMlogVariableName() {
        return remoteName;
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
        creator.createRead(transferVariable, processor, remoteName);
        return transferVariable;
    }

    @Override
    public void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
        creator.createRead(target, processor, remoteName);
    }

    @Override
    public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
        creator.createWrite(value, processor, remoteName);
    }

    @Override
    public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
        valueSetter.accept(transferVariable);
        creator.createWrite(transferVariable, processor, remoteName);
    }

    @Override
    public LogicValue getWriteVariable(ContextfulInstructionCreator creator) {
        return transferVariable;
    }

    @Override
    public void storeValue(ContextfulInstructionCreator creator) {
        creator.createWrite(transferVariable, processor, remoteName);
    }

    public RemoteVariable withProcessor(LogicVariable processor) {
        return new RemoteVariable(sourcePosition, processor, name, remoteName, transferVariable, input, output);
    }
}
