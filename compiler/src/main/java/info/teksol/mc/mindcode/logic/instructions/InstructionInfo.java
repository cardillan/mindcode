package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.LogicKeyword;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/// Contains various classes of instruction information that can be tracked with the instruction
@NullMarked
public enum InstructionInfo {
    /// Additional string identification of the instruction
    MARKER(LogicLabel.EMPTY),

    /// Instruction side effects
    SIDE_EFFECTS(SideEffects.NONE),

    /// Indirect variables accessed by the instruction
    INDIRECT_VARIABLES(List.of()),

    /// Array organization for ReadArr/WriteArr instructions
    ARRAY_ORGANIZATION(ArrayOrganization.NONE),

    /// Type of lookup for lookup arrays
    ARRAY_LOOKUP_TYPE(LogicKeyword.INVALID),

    /// Array construction for ReadArr/WriteArr instructions
    ARRAY_CONSTRUCTION(ArrayConstruction.COMPACT),

    /// This instruction is a compact-array access instruction, setting up the element variable name
    COMPACT_ACCESS_SOURCE(Boolean.FALSE),

    /// This instruction is a compact-array access instruction, using the previously set name of the
    /// element variable. Element name lookup can be skipped.
    COMPACT_ACCESS_TARGET(Boolean.FALSE),

    /// Identifier pairing SETADDR and CALL instruction to facilitate hoisting reversion.
    HOIST_ID(LogicLabel.EMPTY),

    /// Hoisted instruction is marked with Boolean.TRUE. When unrolling loops, a tagged hoisted instruction
    /// is returned to the loop (using the CALL_TAG) to ensure correct processing.
    HOISTED(Boolean.FALSE),

    /// This instruction is a target guard code. Information for the processor emulator to skip executing
    /// this instruction.
    /// Once the emulator is rewritten to operate on plain mlog, it won't be necessary, as the emulator will
    /// properly emulate the target version of the processor.
    TARGET_GUARD(Boolean.FALSE),

    /// An instruction-specific comment
    /// Created, for example, within mlog blocks. Is emitted to the final output at the same line as the instruction
    COMMENT(""),

    /// A list of labels - targets of a string encoded jump table
    JUMP_TABLE(List.of()),

    /// MultiLabel target of a text-based jump table. Needs alignment handling
    JUMP_TARGET(Boolean.FALSE),

    /// Either a call instruction's return address that has been redirected via jump threading
    /// or a return address for a jump instruction which replaces the original call
    CALL_RETURN(LogicLabel.EMPTY),

    /// This operation instruction was already processed by a select optimization
    SELECT_OPERATION(Boolean.FALSE),
    ;

    public final Object defaultValue;

    InstructionInfo(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
