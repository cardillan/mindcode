package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionArgument;
import info.teksol.mindcode.v3.compiler.generation.CodeAssembler;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

// A FunctionArgument representing and output argument to a function call or instruction.
// Provides mechanism to update the value with the output from the function/instruction.
@NullMarked
public class OutputFunctionArgument extends InputFunctionArgument {
    /// Used only for passing the output argument into mlog instructions.
    /// For user function calls, setValue/writeValue are used.
    private final LogicVariable transferVariable;

    public OutputFunctionArgument(AstFunctionArgument argument, ValueStore value, @Nullable LogicVariable transferVariable) {
        super(argument, value);
        this.transferVariable = Objects.requireNonNullElse(transferVariable, LogicVariable.INVALID);

        if (!value.isLvalue()) {
            throw new MindcodeInternalError("Input/output argument must be an l-value.");
        }

        boolean transferVariableProvided = transferVariable != null;
        if (value.isComplex() != transferVariableProvided) {
            throw new MindcodeInternalError("Value is complex and no transfer variable was provided, or vice versa.");
        }
    }

    @Override
    public LogicValue getValue(CodeAssembler assembler) {
        if (value.isComplex()) {
            // Argument is both input and output. Currently not possible, but if such an instruction appears,
            // we'll pass the value from and to a complex store through the placeholder.
            assembler.createInstruction(Opcode.SET, transferVariable, value.getValue(assembler));
            return transferVariable;
        } else {
            return value.getValue(assembler);
        }
    }


    @Override
    public void setValue(CodeAssembler assembler, LogicValue newValue) {
        value.setValue(assembler, newValue);
    }

    @Override
    public void writeValue(CodeAssembler assembler, Consumer<LogicVariable> valueSetter) {
        value.writeValue(assembler, valueSetter);
    }


    @Override
    public LogicValue getWriteVariable(CodeAssembler assembler) {
        return value.isComplex() ? transferVariable : value.getWriteVariable(assembler);
    }

    @Override
    public void storeValue(CodeAssembler assembler) {
        if (value.isComplex()) {
            value.setValue(assembler, transferVariable);
        } else {
            value.storeValue(assembler);
        }
    }
}
