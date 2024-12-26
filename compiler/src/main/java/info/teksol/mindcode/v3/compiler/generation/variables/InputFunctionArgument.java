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
    protected final NodeValue value;

    public InputFunctionArgument(AstFunctionArgument argument, NodeValue value) {
        this.argument = argument;
        this.value = value;
    }

    @Override
    public InputPosition inputPosition() {
        return argument.inputPosition();
    }

    /// Provides the value of the underlying argument
    @Override
    public NodeValue getArgumentValue() {
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
    public LogicValue getTargetVariable(CodeAssembler assembler) {
        // Is only used with function output arguments
        return LogicVariable.INVALID;
    }

    /// Creates code to store output value after instruction call is finished.
    @Override
    public void storeValue(CodeAssembler assembler) {
        // Do nothing - this instance represents an input argument
    }
}
