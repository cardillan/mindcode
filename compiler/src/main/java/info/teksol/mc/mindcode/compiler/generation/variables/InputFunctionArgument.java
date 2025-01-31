package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
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
    public SourcePosition sourcePosition() {
        return argument.sourcePosition();
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
    public void readValue(CodeAssembler assembler, LogicVariable target) {
        value.readValue(assembler, target);
    }

    @Override
    public void setValue(CodeAssembler assembler, LogicValue newValue) {
        assembler.setInternalError();
    }

    @Override
    public void writeValue(CodeAssembler assembler, Consumer<LogicVariable> valueSetter) {
        assembler.setInternalError();
    }

    @Override
    public LogicValue getWriteVariable(CodeAssembler assembler) {
        assembler.setInternalError();
        return LogicVariable.INVALID;
    }

    @Override
    public void storeValue(CodeAssembler assembler) {
        assembler.setInternalError();
    }

    public OutputFunctionArgument toOutputFunctionArgument() {
        return new OutputFunctionArgument(argument, value);
    }
}
