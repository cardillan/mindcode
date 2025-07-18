package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@NullMarked
public interface LogicInstruction extends MlogInstruction {

    AstContext getAstContext();

    default SourcePosition sourcePosition() {
        return getAstContext().sourcePosition();
    }

    LogicInstruction withContext(AstContext astContext);

    Object getInfo(InstructionInfo instructionInfo);

    LogicInstruction setInfo(InstructionInfo instructionInfo, Object value);

    LogicInstruction resetInfo(InstructionInfo instructionInfo);

    LogicInstruction remapInfoLabels(Map<LogicLabel, LogicLabel> labelMap);

    LogicInstruction copyInfo(LogicInstruction other);

    boolean belongsTo(@Nullable AstContext astContext);

    @Nullable AstContext findContextOfType(AstContextType contextType);

    @Nullable AstContext findTopContextOfType(AstContextType contextType);

    default boolean affectsControlFlow() {
        return false;
    }

    default @Nullable LogicVariable getResult() {
        return null;
    }

    default boolean endsCodePath() {
        return false;
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
    /// (e.g. internal array jump tables). Instructions create an unambiguous identifier for each such structure,
    /// and put the size of each such structure into the sharedStructures map.
    ///
    /// @param sharedStructures map to keep the shared structure size. An instruction may increase the size of the
    ///                         shared structure, but not decrease it. When null, the caller isn't interested
    ///                         in shared structure calculation.
    /// @return real size of the instruction
    default int getRealSize(@Nullable Map<String, Integer> sharedStructures) {
        return getOpcode().getSize();
    }

    /// Provides side effects of this instruction
    default ArrayOrganization getArrayOrganization() {
        return (ArrayOrganization) getInfo(InstructionInfo.ARRAY_ORGANIZATION);
    }

    default LogicInstruction setArrayOrganization(ArrayOrganization arrayOrganization) {
        return setInfo(InstructionInfo.ARRAY_ORGANIZATION, arrayOrganization);
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
}
