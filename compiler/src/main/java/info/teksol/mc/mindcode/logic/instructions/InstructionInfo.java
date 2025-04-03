package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jspecify.annotations.NullMarked;

/// Contains various classes of instruction information that can be tracked with the instruction
@NullMarked
public enum InstructionInfo {
    /// Additional string identification of the instruction
    MARKER(LogicLabel.EMPTY),

    /// Instruction side effects
    SIDE_EFFECTS(SideEffects.NONE),

    /// Array organization for ReadArr/WriteArr instructions
    ARRAY_ORGANIZATION(ArrayOrganization.NONE),

    /// Hoisted instruction is marked with Boolean.TRUE. When unrolling loops, a tagged hoisted instruction
    /// is returned to the loop (using the CALL_TAG) to ensure correct processing.
    HOISTED(Boolean.FALSE),
    ;

    public final Object defaultValue;

    InstructionInfo(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
