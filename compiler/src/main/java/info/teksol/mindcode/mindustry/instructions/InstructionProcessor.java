package info.teksol.mindcode.mindustry.instructions;

import info.teksol.mindcode.mindustry.logic.ArgumentType;
import info.teksol.mindcode.mindustry.logic.Opcode;
import info.teksol.mindcode.mindustry.logic.OpcodeVariant;
import info.teksol.mindcode.mindustry.logic.ProcessorEdition;
import info.teksol.mindcode.mindustry.logic.ProcessorVersion;
import info.teksol.mindcode.mindustry.logic.TypedArgument;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public interface InstructionProcessor {

    ProcessorVersion getProcessorVersion();

    ProcessorEdition getProcessorEdition();

    String nextLabel();

    String nextTemp();

    /**
     * @return the list of opcode variants available to this instruction processor.
     */
    List<OpcodeVariant> getOpcodeVariants();

    /**
     * Creates and validates a new LogicInstruction.
     *
     * @param opcode opcode of the instruction
     * @param arguments arguments of the instruction
     * @return a new, validated instance of LogicInstruction
     */
    LogicInstruction createInstruction(Opcode opcode, String... arguments);

    /**
     * Creates and validates a new LogicInstruction.
     *
     * @param opcode opcode of the instruction
     * @param arguments arguments of the instruction
     * @return a new, validated instance of LogicInstruction
     */
    LogicInstruction createInstruction(Opcode opcode, List<String> arguments);

    /**
     * Creates a sample logic instruction from given opcode variant.
     *
     * @param opcodeVariant opcode variant to use
     * @return instruction having all arguments set to opcode variant defaults
     */
    LogicInstruction fromOpcodeVariant(OpcodeVariant opcodeVariant);
    LogicInstruction createInstructionUnchecked(Opcode opcode, List<String> arguments);

    /**
     * Returns a logic instruction with an argument set to the  given value.
     * If the instruction is modified, a new version of it is created, otherwise the current instance is returned.
     *
     * @param instruction instruction to modify
     * @param argIndex index of an argument to set
     * @param value new value for the argument
     * @return a modified instruction
     */
    LogicInstruction replaceArg(LogicInstruction instruction, int argIndex, String value);

    /**
     * Returns a logic instruction with all arguments equal to a specific value replaced by a new value.
     * More than one argument of the instruction can be modified. A new instruction is always created.
     *
     * @param instruction instruction to modify
     * @param oldArg value of arguments to find
     * @param newArg new value for the arguments equal to the old value
     * @return a modified instruction
     */
    LogicInstruction replaceAllArgs(LogicInstruction instruction, String oldArg, String newArg);

    /**
     * Returns list of argument types based on instruction opcode and instruction variant. The variant
     * of the instruction is determined by inspecting its arguments.
     *
     * @param instruction instruction to process
     * @return list of types of given arguments
     */
    List<ArgumentType> getArgumentTypes(LogicInstruction instruction);

    /**
     * Determines the number of input arguments to the instruction.
     *
     * @param instruction instruction to process
     * @return number of input arguments
     */
    int getTotalInputs(LogicInstruction instruction);

    /**
     * Determines the number of output arguments to the instruction.
     *
     * @param instruction instruction to process
     * @return number of output arguments
     */
    int getTotalOutputs(LogicInstruction instruction);

    /**
     * Determines the number of arguments needed to print the instruction
     *
     * @param instruction instruction to process
     * @return number total printable arguments
     */
    int getPrintArgumentCount(LogicInstruction instruction);

    /**
     * Determines the types of arguments based on instruction variant and returns values of arguments
     * which are input in the particular instruction variant.
     *
     * @param instruction instruction to process
     * @return list of argument values assigned to input arguments
     */
    List<String> getInputValues(LogicInstruction instruction);

    /**
     * Determines the types of arguments based on instruction variant and returns values of arguments
     * which are output in the particular instruction variant.
     *
     * @param instruction instruction to process
     * @return list of argument values assigned to output arguments
     */
    List<String> getOutputValues(LogicInstruction instruction);

    /**
     * Assigns types to instruction arguments. Types depend on the opcode and instruction variant. The variant
     * of the instruction is determined by inspecting its arguments.
     *
     * @param instruction instruction to process
     * @return stream of typed arguments
     */
    Stream<TypedArgument> getTypedArguments(LogicInstruction instruction);

    /**
     * Returns true if the given value is allowed to be used in place of the given argument.
     * For input and output arguments, anything is permissible at the moment (it could be a variable name, a literal,
     * or in some cases a @constant), but it might be possible to implement more specific checks in the future.
     * For other arguments, only concrete, version-specific values are permissible.
     * 
     * @param type type of the argument
     * @param value value assigned to the argument
     * @return true if the value is valid for given argument type
     */
    boolean isValid(ArgumentType type, String value);

    /**
     * Translates unary Mindcode operator to Mindustry Logic representation.
     *
     * @param op Mindcode operator
     * @return equivalent Mindustry Logic operation
     */
    String translateUnaryOpToCode(String op);

    /**
     * Translates binary Mindcode operator to Mindustry Logic representation.
     *
     * @param op Mindcode operator
     * @return equivalent Mindustry Logic operation
     */
    String translateBinaryOpToCode(String op);

    /**
     * Returns true if the operation (in Mindustry opcode, ie. lessThan, notEqual etc.) has an inverse.
     *
     * @param comparison operation to inspect
     * @return true if there exist an inverse operation
     */
    boolean hasInverse(String comparison);

    /**
     * Returns the inverse of given operation (in Mindustry opcode, rg. lessThan ==> greaterThanEq).
     *
     * @param comparison operation to invert
     * @return inverse of the given operation (null if it doesn't exist)
     */
    public String getInverse(String comparison);

    /**
     * Returns the set of Mindustry Logic constant names (true, false, null, possibly others).
     * @return the set of constant names
     */
    Set<String> getConstantNames();

    /**
     * Returns the set of possible Mindustry Logic block names (switch, cell, projector etc.).
     * @return the set of block names
     */
    Set<String> getBlockNames();

    default String getLabelPrefix() {
        return "__label";
    }

    default String getTempPrefix() {
        return "__tmp";
    }
}
