package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.logic.LogicBuiltIn;
import info.teksol.mindcode.logic.LogicKeyword;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.generation.CodeAssembler;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public class Property implements ValueStore {
    private final LogicVariable target;
    private final String propertyName;
    private final LogicVariable transferVariable;

    public Property(LogicVariable target, String propertyName, LogicVariable transferVariable) {
        this.target = target;
        this.propertyName = propertyName;
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
        assembler.createSensor(transferVariable, target, LogicBuiltIn.create("@" + propertyName));
        return transferVariable;
    }

    @Override
    public void setValue(CodeAssembler assembler, LogicValue value) {
        assembler.createControl(LogicKeyword.create(propertyName), target, value);
    }

    @Override
    public void writeValue(CodeAssembler assembler, Consumer<LogicVariable> valueSetter) {
        valueSetter.accept(transferVariable);
        assembler.createControl(LogicKeyword.create(propertyName), target, transferVariable);
    }

    @Override
    public LogicValue getWriteVariable(CodeAssembler assembler) {
        return transferVariable;
    }

    @Override
    public void storeValue(CodeAssembler assembler) {
        assembler.createControl(LogicKeyword.create(propertyName), target, transferVariable);
    }
}
