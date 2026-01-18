package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.logic.arguments.LogicString;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
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
    private final boolean cached;
    private @Nullable LogicString remoteNameOverride;

    public RemoteVariable(SourcePosition sourcePosition, LogicVariable processor, String name, LogicString remoteName, LogicVariable transferVariable,
            boolean input, boolean output, boolean cached) {
        this.sourcePosition = sourcePosition;
        this.processor = processor;
        this.name = name;
        this.remoteName = remoteName;
        this.transferVariable = transferVariable;
        this.input = input;
        this.output = output;
        this.cached = cached;
    }

    public LogicVariable getProcessor() {
        return processor;
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
        return Objects.requireNonNullElse(remoteNameOverride, remoteName);
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
        if (cached) {
            return transferVariable;
        }

        LogicVariable tmp = creator.nextTemp();
        creator.createRead(tmp, processor, getMlogVariableName());
        return tmp;
    }

    @Override
    public void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
        if (cached) {
            creator.createSet(target, transferVariable);
        } else {
            creator.createRead(target, processor, getMlogVariableName());
        }
    }

    @Override
    public void initialize(ContextfulInstructionCreator creator) {
        if (cached) {
            creator.createRead(transferVariable, processor, getMlogVariableName());
        }
    }

    @Override
    public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
        if (cached) {
            transferVariable.setValue(creator, value);
            creator.createWrite(transferVariable, processor, getMlogVariableName());
        } else {
            creator.createWrite(value, processor, getMlogVariableName());
        }
    }

    @Override
    public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
        valueSetter.accept(transferVariable);
        creator.createWrite(transferVariable, processor, getMlogVariableName());
    }

    @Override
    public LogicValue getWriteVariable(ContextfulInstructionCreator creator) {
        return transferVariable;
    }

    @Override
    public void storeValue(ContextfulInstructionCreator creator) {
        creator.createWrite(transferVariable, processor, getMlogVariableName());
    }

    public RemoteVariable withProcessor(LogicVariable processor) {
        return new RemoteVariable(sourcePosition, processor, name, getMlogVariableName(), transferVariable, input, output, cached);
    }

    @Override
    public void setArrayElementName(String elementName) {
        this.remoteNameOverride = LogicString.create(elementName);
    }
}
