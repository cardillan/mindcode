package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface InstructionProcessor {

    ProcessorVersion getProcessorVersion();

    ProcessorEdition getProcessorEdition();

    /** @return list of all opcode variants available to this instruction processor */
    List<OpcodeVariant> getOpcodeVariants();

    LogicLabel nextLabel();

    LogicVariable nextTemp();

    LogicVariable nextReturnValue();

    String nextLocalPrefix();


    CallInstruction createCallStackless(LogicAddress address);
    CallRecInstruction createCallRecursive(LogicVariable stack, LogicLabel callAddr, LogicLabel retAddr);
    EndInstruction createEnd();
    GotoInstruction createGoto(LogicVariable address);
    JumpInstruction createJump(LogicLabel label, Condition condition, LogicValue x, LogicValue y);
    JumpInstruction createJumpUnconditional(LogicLabel label);
    LabelInstruction createLabel(LogicLabel label);
    OpInstruction createOp(Operation operation, LogicVariable target, LogicValue first);
    OpInstruction createOp(Operation operation, LogicVariable target, LogicValue first, LogicValue second);
    PopInstruction createPop(LogicVariable memory, LogicVariable value);
    PrintInstruction createPrint(LogicValue what);
    PrintflushInstruction createPrintflush(LogicVariable messageBlock);
    PushInstruction createPush(LogicVariable memory, LogicVariable value);
    ReadInstruction createRead(LogicVariable result, LogicVariable memory, LogicValue index);
    ReturnInstruction createReturn(LogicVariable stack);
    SensorInstruction createSensor(LogicVariable result, LogicValue target, LogicValue property);
    SetInstruction createSet(LogicVariable target, LogicValue value);
    SetAddressInstruction createSetAddress(LogicVariable variable, LogicLabel address);
    StopInstruction createStop();
    WriteInstruction createWrite(LogicValue value, LogicVariable memory, LogicValue index);
    WriteInstruction createWriteAddress(LogicAddress value, LogicVariable memory, LogicValue index);

    /**
     * Creates and validates a new LogicInstruction.
     *
     * @param opcode opcode of the instruction
     * @param arguments arguments of the instruction
     * @return a new, validated instance of LogicInstruction
     */
    LogicInstruction createInstruction(Opcode opcode, LogicArgument... arguments);

    /**
     * Creates and validates a new LogicInstruction.
     *
     * @param opcode opcode of the instruction
     * @param arguments arguments of the instruction
     * @return a new, validated instance of LogicInstruction
     */
    LogicInstruction createInstruction(Opcode opcode, List<LogicArgument> arguments);

    /**
     * Creates a sample logic instruction from given opcode variant.
     *
     * @param opcodeVariant opcode variant to use
     * @return instruction having all arguments set to opcode variant defaults
     */
    LogicInstruction fromOpcodeVariant(OpcodeVariant opcodeVariant);

    /**
     * Creates a logic instruction without validating it. To be used for creating sample instructions.
     *
     * @param opcode opcode of the instruction
     * @param arguments arguments of the instruction
     * @return a new, non-validated instance of LogicInstruction
     */
    LogicInstruction createInstructionUnchecked(Opcode opcode, List<LogicArgument> arguments);

    /**
     * Provides real Mindustry Logic instructions as a replacement for given virtual instruction.
     * Non-virtual instructions are passed as-is.
     *
     * @param instruction instruction to process
     * @param consumer consumer accepting resolved instructions
     */
    void resolve(LogicInstruction instruction, Consumer<LogicInstruction> consumer);

    /**
     * Returns a logic instruction with an argument set to the  given value.
     * If the instruction is modified, a new version of it is created, otherwise the current instance is returned.
     *
     * @param instruction instruction to modify
     * @param argIndex index of an argument to set
     * @param value new value for the argument
     * @return a modified instruction
     */
    LogicInstruction replaceArg(LogicInstruction instruction, int argIndex, LogicArgument value);

    /**
     * Returns a logic instruction with all arguments equal to a specific value replaced by a new value.
     * More than one argument of the instruction can be modified. A new instruction is always created.
     *
     * @param instruction instruction to modify
     * @param oldArg value of arguments to find
     * @param newArg new value for the arguments equal to the old value
     * @return a modified instruction
     */
    LogicInstruction replaceAllArgs(LogicInstruction instruction, LogicArgument oldArg, LogicArgument newArg);

    LogicInstruction replaceArgs(LogicInstruction instruction, List<LogicArgument> newArgs);
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

    default String getRetValPrefix() {
        return "__retval";
    }

    default String getLocalPrefix() {
        return "__fn";
    }
}
