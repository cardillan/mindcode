package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionPrinter;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.mindcode.logic.opcodes.TypedArgument;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

@NullMarked
public interface MlogInstruction {
    /// @return the instruction opcode.
    Opcode getOpcode();

    default String getMlogOpcode() {
        return getOpcode().getOpcode();
    }

    default boolean isSafe() {
        return getOpcode().isSafe();
    }

    /// @return number of input parameters
    int getInputs();

    /// @return number of output parameters
    int getOutputs();

    /// @return all instruction arguments
    List<LogicArgument> getArgs();

    /// @param index argument index
    /// @return argument at the given position
    LogicArgument getArg(int index);

    /// @return list of arguments with their types
    List<TypedArgument> getTypedArguments();

    /// @param index argument index
    /// @return type of the argument at a given position
    InstructionParameterType getArgumentType(int index);

    /// @return list of argument types
    @Nullable
    List<InstructionParameterType> getArgumentTypes();

    /// @return the mlog representation of the instruction
    default String toMlog() {
        return LogicInstructionPrinter.toStringSimple(this);
    }

    /// @return stream of TypedArgument instances
    default Stream<TypedArgument> typedArgumentsStream() {
        return getTypedArguments().stream();
    }

    /// @return stream of arguments assigned to input parameters
    default Stream<LogicArgument> inputArgumentsStream() {
        return getTypedArguments().stream().filter(TypedArgument::isInput).map(TypedArgument::argument);
    }

    /// @return stream of arguments assigned to input-only parameters
    default Stream<LogicArgument> inputOnlyArgumentsStream() {
        return getTypedArguments().stream().filter(TypedArgument::isInputOnly).map(TypedArgument::argument);
    }

    /// @return stream of arguments assigned to output parameters
    default Stream<LogicArgument> outputArgumentsStream() {
        return getTypedArguments().stream().filter(TypedArgument::isOutput).map(TypedArgument::argument);
    }

    /// @return stream of arguments assigned to input or output parameters
    default Stream<LogicArgument> inputOutputArgumentsStream() {
        return getTypedArguments().stream().filter(TypedArgument::isInputOrOutput).map(TypedArgument::argument);
    }

    ///  @return true if the instruction uses the given argument
    default boolean uses(LogicArgument arg) {
        return getArgs().contains(arg);
    }

    ///  @return true if the instruction uses the given argument as an input argument
    default boolean usesAsInput(LogicArgument arg) {
        return inputArgumentsStream().anyMatch(arg::equals);
    }

    ///  @return true if the instruction uses the given argument as an output argument
    default boolean usesAsOutput(LogicArgument arg) {
        return outputArgumentsStream().anyMatch(arg::equals);
    }
}
