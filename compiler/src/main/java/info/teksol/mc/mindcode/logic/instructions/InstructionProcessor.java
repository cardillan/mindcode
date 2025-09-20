package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.MessageEmitter;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicLiteral;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.mindcode.logic.opcodes.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/// The InstructionProcessor interface is responsible for creating, manipulating, and analyzing logic instructions.
/// It provides methods for generating instructions, managing labels and variables, and inspecting or transforming
/// instructions to ensure compatibility, safety, and determinism. This interface forms the foundation for translating
/// and working with Mindustry Logic operations.
///
/// The existing implementations are tailored to different Mindustry Logic versions. Correct implementation is chosen
/// based on the compiler profile, and only generates mlog instructions compatible with the chosen target.
///
/// Variables holding instances of this interface should be named "processor".
@NullMarked
public interface InstructionProcessor extends ContextlessInstructionCreator, MessageEmitter {

    ProcessorVersion getProcessorVersion();

    ProcessorEdition getProcessorEdition();

    /// @return list of all opcode variants available to this instruction processor
    List<OpcodeVariant> getOpcodeVariants();

    MindustryMetadata getMetadata();

    LogicLabel nextLabel();

    LogicLabel nextMarker();

    LogicVariable nextTemp();

    LogicVariable unusedVariable();

    /// Adds a custom-defined block name value.
    void addBlockName(String identifier);

    /// Adds a custom-defined builtin value.
    void addBuiltin(String name);

    /// Adds a custom keyword. Returns true if the category is valid for current target.
    boolean addKeyword(KeywordCategory keywordCategory, String keyword);

    @Nullable
    List<InstructionParameterType> getParameters(Opcode opcode, List<? extends LogicArgument> arguments);

    Collection<String> getParameterValues(InstructionParameterType type);

    /// Creates a sample logic instruction from given opcode variant.
    ///
    /// @param opcodeVariant opcode variant to use
    /// @return instruction having all arguments set to opcode variant defaults
    LogicInstruction fromOpcodeVariant(OpcodeVariant opcodeVariant);

    LogicInstruction createInstructionUnchecked(AstContext context, Opcode opcode, List<LogicArgument> arguments);

    LogicInstruction convertCustomInstruction(LogicInstruction instruction);

    /// Provides real Mindustry Logic instructions as a replacement for given virtual instruction.
    /// Non-virtual instructions are passed as-is.
    ///
    /// @param instruction instruction to process
    /// @param consumer    consumer accepting resolved instructions
    void resolve(LogicInstruction instruction, Consumer<LogicInstruction> consumer);

    /// Creates a copy of the instruction, to be modified using info.
    <T extends LogicInstruction> T copy(T instruction);

    /// Returns a logic instruction with all arguments equal to a specific value replaced by a new value.
    /// More than one argument of the instruction can be modified. A new instruction is always created.
    ///
    /// @param instruction instruction to modify
    /// @param oldArg      value of arguments to find
    /// @param newArg      new value for the arguments equal to the old value
    /// @param <T>         type of instruction being processed
    /// @return a modified instruction
    <T extends LogicInstruction> T replaceAllArgs(T instruction, LogicArgument oldArg, LogicArgument newArg);

    <T extends LogicInstruction> T replaceAllArgs(T instruction, Map<LogicArgument, LogicArgument> argumentMap);

    <T extends LogicInstruction> T replaceArgs(T instruction, List<LogicArgument> newArgs);

    /// Replaces all label arguments of the instruction contained in the label map with the label the original
    /// is mapped to. Used when duplication sections of code containing labels.
    ///
    /// @param instruction instructions to modify
    /// @param labelMap    map assigning new labels to the old ones
    /// @param <T>         type of instruction being processed
    /// @return a modified instruction (original one if modification wasn't necessary)
    <T extends LogicInstruction> T replaceLabels(T instruction, Map<LogicLabel, LogicLabel> labelMap);

    /// Determines the number of arguments needed to print the instruction
    ///
    /// @param instruction instruction to process
    /// @return number total printable arguments
    int getPrintArgumentCount(LogicInstruction instruction);

    /// Determines whether the given instruction is effectively deterministic. The instruction is effectively
    /// deterministic if it sets the output variables to the same values each time it is called, assuming the input
    /// arguments are the same. Side effects aren't considered. Values and character of the input arguments to the
    /// instruction are considered - an otherwise deterministic instruction that has a volatile variable as an input
    /// argument isn't deterministic.
    ///
    /// Note: getlink is not deemed a deterministic instruction, as it can be influenced by linking updating
    /// blocks linked to the processor in the Mindustry World. Block variables are considered volatile
    /// for the same reason.
    ///
    /// @param instruction instruction to inspect
    /// @return true if the instruction is effectively deterministic
    boolean isDeterministic(LogicInstruction instruction);

    /// Returns true if an instruction with given opcode is supported by current processor settings (version, edition).
    ///
    /// @param opcode instruction opcode
    /// @return true if the instruction is supported
    boolean isSupported(Opcode opcode);

    /// Returns true if an instruction with given opcode and arguments is supported by current processor
    /// settings (version, edition).
    ///
    /// @param opcode    instruction opcode
    /// @param arguments instruction arguments
    /// @return true if the instruction is supported
    boolean isSupported(Opcode opcode, List<LogicArgument> arguments);

    /// Determines whether the identifier could be a block name (such as switch1, cell2, projector3 etc.).
    ///
    /// @param identifier identifier to check
    /// @return true if it conforms to Mindustry Logic block name
    boolean isBlockName(String identifier);

    /// Determines whether the string is an identifier denoting a main (global) variable.
    ///
    /// @param identifier identifier to check
    /// @return true if the identifier denotes a main variable
    boolean isGlobalName(String identifier);

    /// Determines whether this identifier corresponds to a known built-in value.
    ///
    /// @param builtin name of the built-in variable including `@` prefix
    /// @return true if the built-in variable is volatile.
    boolean isValidBuiltIn(String builtin);

    /// Determines whether this identifier corresponds to a volatile built-in value.
    ///
    /// @param builtin name of the built-in variable including `@` prefix
    /// @return true if the built-in variable is volatile.
    boolean isVolatileBuiltIn(String builtin);

    /// Mindustry is not capable of parsing some literals in non-decimal bases. This method identifies such numbers,
    /// so that they can be encoded into mlog as decimals.
    boolean isValidHexLiteral(long value);

    /// Prior to version 8, Mindustry Logic was unable to parse some specific integer literal values in all bases.
    /// This method identifies supported and unsupported integer literals.
    boolean isValidIntegerLiteral(long value);

    /// Rewrites the literal to conform to mlog limitations. If such a conversion isn't possible, an empty optional
    /// is returned.
    ///
    /// @param sourcePosition     the source position of the literal
    /// @param literal            the literal to process
    /// @param allowPrecisionLoss `true` to format literals leading to precision loss (emits a warning),
    ///                           `false` to refuse formating such literals
    /// @return Optional containing mlog compatible literal, or nothing if mlog compatible equivalent doesn't exist
    Optional<String> mlogRewrite(SourcePosition sourcePosition, String literal, boolean allowPrecisionLoss);

    Optional<LogicLiteral> createLiteral(SourcePosition sourcePosition, double value, boolean allowPrecisionLoss);

    /// Formats number for text output
    ///
    /// @return text representation of the number as generated by Mindustry Logic
    String formatNumber(double value);

    double parseNumber(String literal);

    default String getLabelPrefix() {
        return "*label";
    }
}
