package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.logic.arguments.LogicBuiltIn;
import info.teksol.mc.mindcode.logic.arguments.LogicKeyword;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public class Property implements ValueStore {
    private final LogicValue target;
    private final String propertyName;
    private final LogicVariable transferVariable;

    public Property(LogicValue target, String propertyName, LogicVariable transferVariable) {
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