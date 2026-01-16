package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.profile.LocalCompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

@NullMarked
public interface LogicInstruction extends MlogInstruction {

    AstContext getAstContext();

    default SourcePosition sourcePosition() {
        return getAstContext().sourcePosition();
    }

    LogicInstruction withContext(AstContext context);

    Object getInfo(InstructionInfo instructionInfo);

    LogicInstruction setInfo(InstructionInfo instructionInfo, Object value);

    LogicInstruction resetInfo(InstructionInfo instructionInfo);

    LogicInstruction remapInfoLabels(Map<LogicLabel, LogicLabel> labelMap);

    LogicInstruction remapInfoValues(Map<? extends LogicValue, LogicValue> valueMap);

    <T extends LogicInstruction> T copyInfo(T other);

    boolean belongsTo(@Nullable AstContext context);

    @Nullable AstContext findSuperContextOfType(AstContextType contextType);

    default boolean affectsControlFlow() {
        return false;
    }

    default @Nullable LogicVariable getResult() {
        return null;
    }

    default boolean endsCodePath() {
        return false;
    }

    default void processAllModifications(Consumer<LogicArgument> action) {
        outputArgumentsStream().forEach(action);
        getSideEffects().resets().forEach(action);
        getSideEffects().writes().forEach(action);
    }

    default Stream<LogicArgument> getAllReads() {
        List<LogicVariable> reads = getSideEffects().reads();
        return reads.isEmpty() ? inputOnlyArgumentsStream() : Stream.concat(inputArgumentsStream(), reads.stream());
    }

    default Stream<LogicArgument> getAllWrites() {
        SideEffects s = getSideEffects();
        return s.writes().isEmpty() && s.resets().isEmpty() ? outputArgumentsStream()
                : Stream.of(outputArgumentsStream(), s.writes().stream(), s.resets().stream()).mapMulti(Stream::forEach);
    }

    default Stream<LogicArgument> getAllArguments() {
        List<LogicVariable> all = getSideEffects().all();
        return all.isEmpty() ? inputOutputArgumentsStream() : Stream.concat(inputArgumentsStream(), all.stream());
    }

    /// Indicates the instruction is real - produces an actual code. Non-real (virtual) instructions
    /// include labels and no-op.
    default boolean isReal() {
        return getOpcode().getSize() > 0;
    }

    /// Returns the true size of the instruction. Real instructions have a size of 1, virtual instruction may get
    /// resolved to more (or less) real instructions.
    ///
    /// Some instructions might implicitly generate code that can be shared among several different instructions
    /// (e.g., internal array jump tables). Instructions create an unambiguous identifier for each such structure
    /// and put the size of each such structure into the sharedStructures map.
    ///
    /// @param sharedStructures map to keep the shared structure size. An instruction may increase the size of the
    ///                         shared structure but not decrease it. When null, the caller isn't interested
    ///                         in shared structure calculation.
    /// @return real size of the instruction
    default int getSharedSize(@Nullable Map<String, Integer> sharedStructures) {
        return getOpcode().getSize();
    }

    /// Returns the true size of the instruction without calculating the size of shared structures.
    /// Real instructions have a size of 1, virtual instruction may get resolved to more (or less) real instructions.
    ///
    /// @return real size of the instruction
    default int getRealSize() {
        return getSharedSize(null);
    }

    default LogicLabel getMarker() {
        return (LogicLabel) getInfo(InstructionInfo.MARKER);
    }

    default LogicInstruction setMarker(LogicLabel marker) {
        return setInfo(InstructionInfo.MARKER, marker);
    }

    default LogicLabel getHoistId() {
        return (LogicLabel) getInfo(InstructionInfo.HOIST_ID);
    }

    default boolean isHoisted() {
        return (boolean) getInfo(InstructionInfo.HOISTED);
    }

    default LogicInstruction setHoisted(boolean hoisted) {
        return setInfo(InstructionInfo.HOISTED, hoisted);
    }

    default LogicInstruction setHoistId(LogicLabel marker) {
        return setInfo(InstructionInfo.HOIST_ID, marker);
    }

    default LogicInstruction resetHoistId() {
        return resetInfo(InstructionInfo.HOIST_ID).resetInfo(InstructionInfo.HOISTED);
    }

    /// Provides side effects of this instruction
    default SideEffects getSideEffects() {
        return (SideEffects) getInfo(InstructionInfo.SIDE_EFFECTS);
    }

    default LogicInstruction setSideEffects(SideEffects sideEffects) {
        return setInfo(InstructionInfo.SIDE_EFFECTS, sideEffects);
    }
    
    @SuppressWarnings("unchecked")
    default List<LogicVariable> getIndirectVariables() {
        return (List<LogicVariable>) getInfo(InstructionInfo.INDIRECT_VARIABLES);
    }

    default LogicInstruction setIndirectVariables(List<LogicVariable> indirectVariables) {
        return setInfo(InstructionInfo.INDIRECT_VARIABLES, indirectVariables);
    }

    default LogicInstruction resetIndirectVariables() {
        return resetInfo(InstructionInfo.INDIRECT_VARIABLES);
    }

    default boolean isTargetGuard() {
        return (boolean) getInfo(InstructionInfo.TARGET_GUARD);
    }

    default LogicInstruction setTargetGuard(boolean targetGuard) {
        return setInfo(InstructionInfo.TARGET_GUARD, targetGuard);
    }

    default String getComment() {
        return (String) getInfo(InstructionInfo.COMMENT);
    }

    default LogicInstruction setComment(String comment) {
        return setInfo(InstructionInfo.COMMENT, comment);
    }

    default LogicInstruction copyComment(LogicInstruction ix) {
        return setInfo(InstructionInfo.COMMENT, ix.getComment());
    }

    @SuppressWarnings("unchecked")
    default List<LogicLabel> getJumpTable() {
        return (List<LogicLabel>) getInfo(InstructionInfo.JUMP_TABLE);
    }

    default LogicInstruction setJumpTable(List<LogicLabel> jumpTable) {
        return setInfo(InstructionInfo.JUMP_TABLE, jumpTable);
    }

    default boolean isJumpTarget() {
        return (boolean) getInfo(InstructionInfo.JUMP_TARGET);
    }

    default LogicInstruction setJumpTarget() {
        return setInfo(InstructionInfo.JUMP_TARGET, true);
    }

    default LogicInstruction setJumpTarget(boolean jumpTarget) {
        return setInfo(InstructionInfo.JUMP_TARGET, jumpTarget);
    }

    default LogicLabel getCallReturn() {
        return (LogicLabel) getInfo(InstructionInfo.CALL_RETURN);
    }

    default LogicInstruction setCallReturn(LogicLabel callReturn) {
        return setInfo(InstructionInfo.CALL_RETURN, callReturn);
    }

    default boolean isSelectOperation() {
        return (boolean) getInfo(InstructionInfo.SELECT_OPERATION);
    }

    default LogicInstruction setSelectOperation() {
        return setInfo(InstructionInfo.SELECT_OPERATION, true);
    }

    default boolean isFallThrough() {
        return (boolean) getInfo(InstructionInfo.FALL_THROUGH);
    }

    default LogicInstruction setFallThrough(boolean fallThrough) {
        return setInfo(InstructionInfo.FALL_THROUGH, fallThrough);
    }

    default boolean isFixedMultilabel() {
        return (boolean) getInfo(InstructionInfo.FIXED_MULTILABEL);
    }

    default LogicInstruction setFixedMultilabel(boolean fixedMultilabel) {
        return setInfo(InstructionInfo.FIXED_MULTILABEL, fixedMultilabel);
    }

    default LocalCompilerProfile getLocalProfile() {
        return getAstContext().getLocalProfile();
    }
}
