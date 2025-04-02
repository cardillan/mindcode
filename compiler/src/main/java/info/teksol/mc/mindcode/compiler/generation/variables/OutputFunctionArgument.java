package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
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

    /// Special case to handle @wait
    OutputFunctionArgument(AstFunctionArgument argument, ValueStore value) {
        super(argument, value);
        this.transferVariable = LogicVariable.INVALID;
    }

    @Override
    public boolean isLvalue() {
        return value.isLvalue();
    }

    @Override
    public LogicValue getValue(ContextfulInstructionCreator creator) {
        if (value.isComplex()) {
            // Argument is both input and output. Currently not possible, but if such an instruction appears,
            // we'll pass the value from and to a complex store through the placeholder.
            creator.createInstruction(Opcode.SET, transferVariable, value.getValue(creator));
            return transferVariable;
        } else {
            return value.getValue(creator);
        }
    }

    @Override
    public void setValue(ContextfulInstructionCreator creator, LogicValue newValue) {
        value.setValue(creator, newValue);
    }

    @Override
    public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
        value.writeValue(creator, valueSetter);
    }


    @Override
    public LogicValue getWriteVariable(ContextfulInstructionCreator creator) {
        return value.isComplex() ? transferVariable : value.getWriteVariable(creator);
    }

    @Override
    public void storeValue(ContextfulInstructionCreator creator) {
        if (value.isComplex()) {
            value.setValue(creator, transferVariable);
        } else {
            value.storeValue(creator);
        }
    }
}
