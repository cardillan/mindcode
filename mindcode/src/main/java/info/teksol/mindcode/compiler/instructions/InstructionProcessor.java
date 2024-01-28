package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public interface InstructionProcessor {

    ProcessorVersion getProcessorVersion();

    ProcessorEdition getProcessorEdition();

    /** @return list of all opcode variants available to this instruction processor */
    List<OpcodeVariant> getOpcodeVariants();

    LogicLabel nextLabel();
    LogicVariable nextTemp();
    String nextFunctionPrefix();

    /**
     * Creates a sample logic instruction from given opcode variant.
     *
     * @param opcodeVariant opcode variant to use
     * @return instruction having all arguments set to opcode variant defaults
     */
    LogicInstruction fromOpcodeVariant(OpcodeVariant opcodeVariant);


    CallInstruction createCallStackless(AstContext astContext, LogicAddress address);
    CallRecInstruction createCallRecursive(AstContext astContext, LogicVariable stack, LogicLabel callAddr, LogicLabel retAddr);
    EndInstruction createEnd(AstContext astContext);
    GotoInstruction createGoto(AstContext astContext, LogicVariable address, LogicLabel marker);
    GotoOffsetInstruction createGotoOffset(AstContext astContext, LogicLabel target, LogicVariable value, LogicNumber offset, LogicLabel marker);
    JumpInstruction createJump(AstContext astContext, LogicLabel target, Condition condition, LogicValue x, LogicValue y);
    JumpInstruction createJumpUnconditional(AstContext astContext, LogicLabel target);
    LabelInstruction createLabel(AstContext astContext, LogicLabel label);
    GotoLabelInstruction createGotoLabel(AstContext astContext, LogicLabel label, LogicLabel marker);
    NoOpInstruction createNoOp(AstContext astContext);
    OpInstruction createOp(AstContext astContext, Operation operation, LogicVariable target, LogicValue first);
    OpInstruction createOp(AstContext astContext, Operation operation, LogicVariable target, LogicValue first, LogicValue second);
    PopInstruction createPop(AstContext astContext, LogicVariable memory, LogicVariable value);
    PrintInstruction createPrint(AstContext astContext, LogicValue what);
    PrintflushInstruction createPrintflush(AstContext astContext, LogicVariable messageBlock);
    PushInstruction createPush(AstContext astContext, LogicVariable memory, LogicVariable value);
    ReadInstruction createRead(AstContext astContext, LogicVariable result, LogicVariable memory, LogicValue index);
    ReturnInstruction createReturn(AstContext astContext, LogicVariable stack);
    SensorInstruction createSensor(AstContext astContext, LogicVariable result, LogicValue target, LogicValue property);
    SetInstruction createSet(AstContext astContext, LogicVariable target, LogicValue value);
    SetAddressInstruction createSetAddress(AstContext astContext, LogicVariable variable, LogicLabel address);
    StopInstruction createStop(AstContext astContext);
    WriteInstruction createWrite(AstContext astContext, LogicValue value, LogicVariable memory, LogicValue index);
    LogicInstruction createInstruction(AstContext astContext, Opcode opcode, LogicArgument... arguments);
    LogicInstruction createInstruction(AstContext astContext, Opcode opcode, List<LogicArgument> arguments);
    LogicInstruction createInstructionUnchecked(AstContext context, Opcode opcode, List<LogicArgument> arguments);

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
