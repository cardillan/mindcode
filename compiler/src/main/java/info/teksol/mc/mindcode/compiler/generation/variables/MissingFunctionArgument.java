package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
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
    public void readValue(CodeAssembler assembler, LogicVariable target) {
        // Do nothing
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
