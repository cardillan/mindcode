package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.messages.MessageEmitter;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * The InstructionProcessor interface is responsible for creating, manipulating, and analyzing logic instructions.
 * It provides methods for generating instructions, managing labels and variables, and inspecting or transforming
 * instructions to ensure compatibility, safety, and determinism. This interface forms the foundation for translating
 * and working with Mindustry Logic operations.
 * <p>
 * The existing implementations are tailored to different Mindustry Logic versions. Correct implementation is chosen
 * based on the compiler profile, and only generates mlog instructions compatible with the chosen target.
 * <p>
 *
 * Variables holding instances of this interface should be named "processor".
 */
public interface InstructionProcessor extends ContextlessInstructionCreator, MessageEmitter {

    ProcessorVersion getProcessorVersion();

    ProcessorEdition getProcessorEdition();

    /** @return list of all opcode variants available to this instruction processor */
    List<OpcodeVariant> getOpcodeVariants();

    LogicLabel nextLabel();
    LogicLabel nextMarker();
    LogicVariable nextTemp();
    String nextFunctionPrefix();
    LogicVariable unusedVariable();

    List<InstructionParameterType> getParameters(Opcode opcode, List<? extends LogicArgument> arguments);

    Collection<String> getParameterValues(InstructionParameterType type);

    /**
     * Creates a sample logic instruction from given opcode variant.
     *
     * @param opcodeVariant opcode variant to use
     * @return instruction having all arguments set to opcode variant defaults
     */
    LogicInstruction fromOpcodeVariant(OpcodeVariant opcodeVariant);

    LogicInstruction createInstructionUnchecked(AstContext context, Opcode opcode, List<LogicArgument> arguments);

    LogicInstruction convertCustomInstruction(LogicInstruction instruction);

    /**
     * Provides real Mindustry Logic instructions as a replacement for given virtual instruction.
     * Non-virtual instructions are passed as-is.
     *
     * @param instruction instruction to process
     * @param consumer consumer accepting resolved instructions
     */
    void resolve(LogicInstruction instruction, Consumer<LogicInstruction> consumer);

    /**
     * Returns a logic instruction with all arguments equal to a specific value replaced by a new value.
     * More than one argument of the instruction can be modified. A new instruction is always created.
     *
     * @param instruction instruction to modify
     * @param oldArg value of arguments to find
     * @param newArg new value for the arguments equal to the old value
     * @return a modified instruction
     * @param <T> type of instruction being processed
     */
    <T extends LogicInstruction> T replaceAllArgs(T instruction, LogicArgument oldArg, LogicArgument newArg);

    <T extends LogicInstruction> T replaceAllArgs(T instruction, Map<LogicArgument, LogicArgument> argumentMap);

    <T extends LogicInstruction> T replaceArgs(T instruction, List<LogicArgument> newArgs);

    /**
     * Replaces all label arguments of the instruction contained in the label map with the label the original
     * is mapped to. Used when duplication sections of code containing labels.
     *
     * @param instruction instructions to modify
     * @param labelMap map assigning new labels to the old ones
     * @return a modified instruction (original one if modification wasn't necessary)
     * @param <T> type of instruction being processed
     */
    <T extends LogicInstruction> T replaceLabels(T instruction, Map<LogicLabel, LogicLabel> labelMap);

    /**
     * Determines the number of arguments needed to print the instruction
     *
     * @param instruction instruction to process
     * @return number total printable arguments
     */
    int getPrintArgumentCount(LogicInstruction instruction);

    /**
     * Determines whether the given instruction is effectively deterministic. The instruction is effectively
     * deterministic if it sets the output variables to the same values each time it is called, assuming the input
     * arguments are the same. Side effects aren't considered. Values and character of the input arguments to the
     * instruction are considered - an otherwise deterministic instruction that has a volatile variable as an input
     * argument isn't deterministic.
     * <p>
     * Note: getlink is not deemed a deterministic instruction, as it can be influenced by linking updating
     * blocks linked to the processor in the Mindustry World. Block variables are considered volatile
     * for the same reason.
     *
     * @param instruction instruction to inspect
     * @return true if the instruction is effectively deterministic
     */
    boolean isDeterministic(LogicInstruction instruction);

    /**
     * Determines whether the instruction is safe. A safe instruction has no side effects, i.e. performs no action
     * apart from changing values of output arguments. Any instruction that modifies the state of the Mindustry world
     * is not safe.
     *
     * @param instruction instruction to inspect
     * @return true if the instruction is safe
     */
    boolean isSafe(LogicInstruction instruction);

    /**
     * Returns true if an instruction with given opcode and arguments is supported by current processor
     * settings (version, edition).
     *
     * @param opcode instruction opcode
     * @param arguments instruction arguments
     * @return true if the instruction is supported
     */
    boolean isSupported(Opcode opcode, List<LogicArgument> arguments);

    /**
     * Determines whether the identifier could be a block name (such as switch1, cell2, projector3 etc.).
     *
     * @param identifier identifier to check
     * @return true if it conforms to Mindustry Logic block name
     */
    boolean isBlockName(String identifier);

    /**
     * Determines whether the string is an identifier denoting a main (global) variable.
     *
     * @param identifier identifier to check
     * @return true if the identifier denotes a main variable
     */
    boolean isGlobalName(String identifier);

    /**
     * Rewrites the literal to conform to mlog limitations. If such a conversion isn't possible, an empty optional
     * is returned.
     *
     * @param literal the literal to process
     * @return Optional containing mlog compatible literal, or nothing if mlog compatible equivalent doesn't exist
     */
    Optional<String> mlogRewrite(String literal);

    Optional<String> mlogFormat(double value);

    String formatNumber(double value);

    default String getLabelPrefix() {
        return "__label";
    }

    default String getTempPrefix() {
        return "__tmp";
    }

    default String getFunctionPrefix() {
        return "__fn";
    }
}