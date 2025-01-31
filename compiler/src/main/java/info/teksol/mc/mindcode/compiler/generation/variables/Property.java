package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.logic.arguments.LogicBuiltIn;
import info.teksol.mc.mindcode.logic.arguments.LogicKeyword;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
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
    public LogicValue getValue(CodeAssembler assembler) {
        assembler.createSensor(transferVariable, object,
                LogicBuiltIn.create(assembler.getProcessor(), sourcePosition, "@" + propertyName));
        return transferVariable;
    }

    @Override
    public void readValue(CodeAssembler assembler, LogicVariable result) {
        assembler.createSensor(result, object,
                LogicBuiltIn.create(assembler.getProcessor(), sourcePosition, "@" + propertyName));
    }

    @Override
    public void setValue(CodeAssembler assembler, LogicValue value) {
        assembler.createControl(LogicKeyword.create(propertyName), object, value);
    }

    @Override
    public void writeValue(CodeAssembler assembler, Consumer<LogicVariable> valueSetter) {
        valueSetter.accept(transferVariable);
        assembler.createControl(LogicKeyword.create(propertyName), object, transferVariable);
    }

    @Override
    public LogicValue getWriteVariable(CodeAssembler assembler) {
        return transferVariable;
    }

    @Override
    public void storeValue(CodeAssembler assembler) {
        assembler.createControl(LogicKeyword.create(propertyName), object, transferVariable);
    }
}
