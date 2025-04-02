package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.logic.arguments.LogicBuiltIn;
import info.teksol.mc.mindcode.logic.arguments.LogicKeyword;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public class Property implements ValueStore {
    private final SourcePosition sourcePosition;
    private final LogicValue object;
    private final String propertyName;
    private final LogicVariable transferVariable;

    public Property(SourcePosition sourcePosition, LogicValue object, String propertyName, LogicVariable transferVariable) {
        this.sourcePosition = sourcePosition;
        this.object = object;
        this.propertyName = propertyName;
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
        creator.createSensor(transferVariable, object,
                LogicBuiltIn.create(creator.getProcessor(), sourcePosition, "@" + propertyName));
        return transferVariable;
    }

    @Override
    public void readValue(ContextfulInstructionCreator creator, LogicVariable result) {
        creator.createSensor(result, object,
                LogicBuiltIn.create(creator.getProcessor(), sourcePosition, "@" + propertyName));
    }

    @Override
    public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
        creator.createControl(LogicKeyword.create(sourcePosition, propertyName), object, value);
    }

    @Override
    public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
        valueSetter.accept(transferVariable);
        creator.createControl(LogicKeyword.create(sourcePosition, propertyName), object, transferVariable);
    }

    @Override
    public LogicValue getWriteVariable(ContextfulInstructionCreator creator) {
        return transferVariable;
    }

    @Override
    public void storeValue(ContextfulInstructionCreator creator) {
        creator.createControl(LogicKeyword.create(sourcePosition, propertyName), object, transferVariable);
    }
}
