package info.teksol.mc.mindcode.logic.instructions;

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

    /// Array organization for ReadArr/WriteArr instructions
    ARRAY_ORGANIZATION(ArrayOrganization.NONE),

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

    /// A call instruction's return address that has been redirected via jump threading
    CALL_RETURN(LogicLabel.EMPTY),
    ;

    public final Object defaultValue;

    InstructionInfo(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
