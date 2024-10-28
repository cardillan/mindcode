package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.LogicInstructionPrinter;
import info.teksol.mindcode.logic.InstructionParameterType;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.Opcode;
import info.teksol.mindcode.logic.TypedArgument;

import java.util.List;
import java.util.stream.Stream;

public interface MlogInstruction {
    /**
     * @return the instruction opcode.
     */
    Opcode getOpcode();

    default String getMlogOpcode() {
        return getOpcode().getOpcode();
    }

    default boolean isSafe() {
        return getOpcode().isSafe();
    }

    /**
     * @return number of input parameters
     */
    int getInputs();

    /**
     * @return number of output parameters
     */
    int getOutputs();

    /**
     * @return all instruction arguments
     */
    List<LogicArgument> getArgs();

    /**
     * @param index argument index
     * @return argument at the given position
     */
    LogicArgument getArg(int index);

    /**
     * @return list of arguments with their types
     */
    List<TypedArgument> getTypedArguments();

    /**
     * @param index argument index
     * @return type of the argument at given position
     */
    InstructionParameterType getArgumentType(int index);

    /**
     * @return list of arguments types
     */
    List<InstructionParameterType> getArgumentTypes();

    /**
     * Returns the true size of the instruction. Real instructions have a size of 1, virtual instruction may get
     * resolved to more (or less) real instructions.
     *
     * @return real size of the instruction
     */
    default int getRealSize() {
        return getOpcode().getSize();
    }

    /**
     * @return the mlog representation of the instruction
     */
    default String toMlog() {
        return LogicInstructionPrinter.toStringSimple(this);
    }

    /**
     * @return stream of TypedArgument instances
     */
    default Stream<TypedArgument> typedArgumentsStream() {
        return getTypedArguments().stream();
    }

    /**
     * @return stream of arguments assigned to input parameters
     */
    default Stream<LogicArgument> inputArgumentsStream() {
        return getTypedArguments().stream().filter(TypedArgument::isInput).map(TypedArgument::argument);
    }

    /**
     * @return stream of arguments assigned to output parameters
     */
    default Stream<LogicArgument> outputArgumentsStream() {
        return getTypedArguments().stream().filter(TypedArgument::isOutput).map(TypedArgument::argument);
    }

    /**
     * @return stream of arguments assigned to input or output parameters
     */
    default Stream<LogicArgument> inputOutputArgumentsStream() {
        return getTypedArguments().stream().filter(TypedArgument::isInputOrOutput).map(TypedArgument::argument);
    }
}
