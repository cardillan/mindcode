package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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

    /// Indicates the instructions is real - produces an actual code. Non-real (virtual) instructions
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

    /// Provides side effects of this instruction
    default SideEffects getSideEffects() {
        return (SideEffects) getInfo(InstructionInfo.SIDE_EFFECTS);
    }

    default LogicInstruction setSideEffects(SideEffects sideEffects) {
        return setInfo(InstructionInfo.SIDE_EFFECTS, sideEffects);
    }
}
