package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

/// Represents a missing function argument.
@NullMarked
public class MissingFunctionArgument implements FunctionArgument {
    protected final AstFunctionArgument argument;

    public MissingFunctionArgument(AstFunctionArgument argument) {
        this.argument = argument;
        if (argument.hasExpression()) {
            throw new MindcodeInternalError("Argument with expression passed into MissingFunctionArgument.");
        }
    }

    @Override
    public SourcePosition sourcePosition() {
        return argument.sourcePosition();
    }

    @Override
    public ValueStore unwrap() {
        return LogicVariable.INVALID;
    }

    @Override
    public boolean hasValue() {
        return false;
    }

    @Override
    public boolean hasInModifier() {
        return argument.hasInModifier();
    }

    @Override
    public boolean hasOutModifier() {
        return argument.hasOutModifier();
    }

    public boolean hasRefModifier() {
        return argument.hasRefModifier();
    }

    @Override
    public boolean isInput() {
        return true;
    }

    @Override
    public boolean isOutput() {
        return false;
    }

    @Override
    public boolean isComplex() {
        return false;
    }

    @Override
    public boolean isLvalue() {
        return false;
    }

    @Override
    public LogicValue getValue(ContextfulInstructionCreator creator) {
        return LogicVariable.INVALID;
    }

    @Override
    public void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
        // Do nothing
    }

    @Override
    public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
        // Do nothing
    }

    @Override
    public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
        // Do nothing
    }

    @Override
    public LogicVariable getWriteVariable(ContextfulInstructionCreator creator) {
        return LogicVariable.INVALID;
    }
}
