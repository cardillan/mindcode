package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionArgument;
import info.teksol.mindcode.v3.compiler.generation.CodeAssembler;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public class InputFunctionArgument implements FunctionArgument {
    protected final AstFunctionArgument argument;
    protected final ValueStore value;

    public InputFunctionArgument(AstFunctionArgument argument, ValueStore value) {
        this.argument = argument;
        this.value = value;
    }

    @Override
    public InputPosition inputPosition() {
        return argument.inputPosition();
    }

    /// Provides the value of the underlying argument
    @Override
    public ValueStore getArgumentValue() {
        return value;
    }

    @Override
    public boolean hasValue() {
        return argument.hasExpression();
    }

    @Override
    public boolean hasInModifier() {
        return argument.hasInModifier();
    }

    @Override
    public boolean hasOutModifier() {
        return argument.hasOutModifier();
    }

    @Override
    public boolean isOutput() {
        return argument.isOutput();
    }

    @Override
    public boolean isComplex() {
        return value.isComplex();
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

    @Override
    public LogicValue getWriteVariable(CodeAssembler assembler) {
        return value.getWriteVariable(assembler);
    }

    @Override
    public void storeValue(CodeAssembler assembler) {
        value.storeValue(assembler);
    }
}
