package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.ContextFactory;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicString;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
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
    public ValueStore unwrap() {
        return value.unwrap();
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
    public boolean hasRefModifier() {
        return argument.hasRefModifier();
    }

    @Override
    public boolean isInput() {
        return argument.isInput();
    }

    @Override
    public boolean isOutput() {
        return argument.isOutput();
    }

    @Override
    public LogicString getMlogVariableName() {
        return value.getMlogVariableName();
    }

    @Override
    public boolean isComplex() {
        return value.isComplex();
    }

    @Override
    public boolean isLvalue() {
        return false;
    }

    @Override
    public LogicValue getValue(ContextfulInstructionCreator creator) {
        return value.getValue(creator);
    }

    @Override
    public void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
        value.readValue(creator, target);
    }

    @Override
    public void setValue(ContextfulInstructionCreator creator, LogicValue newValue) {
        ContextFactory.getMessageContext().error(argument, ERR.LVALUE_CANNOT_ASSIGN_TO_ARGUMENT);
    }

    @Override
    public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
        creator.setInternalError();
    }

    @Override
    public LogicValue getWriteVariable(ContextfulInstructionCreator creator) {
        creator.setInternalError();
        return LogicVariable.INVALID;
    }

    @Override
    public void storeValue(ContextfulInstructionCreator creator) {
        creator.setInternalError();
    }

    public OutputFunctionArgument toOutputFunctionArgument() {
        return new OutputFunctionArgument(argument, value);
    }
}
