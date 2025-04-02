package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;

/// Contains various classes of instruction information that can be tracked with the instruction
public enum InstructionInfo {
    /// Additional string identification of the instruction
    MARKER(LogicLabel.EMPTY),

    /// Instruction side effects
    SIDE_EFFECTS(SideEffects.NONE),

    /// Array organization for ReadArr/WriteArr instructions
    ARRAY_ORGANIZATION(ArrayOrganization.NONE),
    ;

    public final Object defaultValue;

    InstructionInfo(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
