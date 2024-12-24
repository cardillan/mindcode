package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.generation.CodeAssembler;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public class FunctionArgument implements NodeValue {
    private final NodeValue value;
    private final boolean inModifier;
    private final boolean outModifier;

    public FunctionArgument(NodeValue value, boolean inModifier, boolean outModifier) {
        this.value = value;
        this.inModifier = inModifier;
        this.outModifier = outModifier;
    }

    @Override
    public boolean hasInModifier() {
        return inModifier;
    }

    @Override
    public boolean hasOutModifier() {
        return outModifier;
    }

    @Override
    public boolean isLvalue() {
        return value.isLvalue();
    }

    @Override
    public LogicValue getValue(CodeAssembler assembler) {
        return value.getValue(assembler);
    }

    @Override
    public void setValue(CodeAssembler assembler, LogicValue value) {
        this.value.setValue(assembler, value);
    }

    @Override
    public void writeValue(CodeAssembler assembler, Consumer<LogicVariable> valueSetter) {
        value.writeValue(assembler, valueSetter);
    }
}
