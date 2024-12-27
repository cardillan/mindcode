package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionArgument;
import info.teksol.mindcode.v3.compiler.generation.CodeAssembler;
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
    public InputPosition inputPosition() {
        return argument.inputPosition();
    }

    @Override
    public ValueStore getArgumentValue() {
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
    public LogicValue getValue(CodeAssembler assembler) {
        return LogicVariable.INVALID;
    }

    @Override
    public void setValue(CodeAssembler assembler, LogicValue value) {
        // Do nothing
    }

    @Override
    public void writeValue(CodeAssembler assembler, Consumer<LogicVariable> valueSetter) {
        // Do nothing
    }

    @Override
    public LogicVariable getWriteVariable(CodeAssembler assembler) {
        return LogicVariable.INVALID;
    }

    /// Creates code to store output value after instruction call is finished.
    @Override
    public void storeValue(CodeAssembler assembler) {
        // Do nothing - this instance represents an unspecified output argument
    }
}
