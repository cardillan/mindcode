package info.teksol.mc.emulator.processor;

import java.util.EnumSet;
import java.util.List;

public enum ExecutionFlag {
    TRACE_EXECUTION                 ("output instruction and variable states at each execution step", true, false),
    DUMP_VARIABLES_ON_STOP          ("output variable values when the 'stop' instruction is encountered"),

    STOP_ON_STOP_INSTRUCTION        ("stop execution when the 'stop' instruction is encountered"),
    STOP_ON_END_INSTRUCTION         ("stop execution when the 'end' instruction is encountered"),
    STOP_ON_PROGRAM_END             ("stop execution when the end of instruction list is reached"),
    ERR_EXECUTION_LIMIT_EXCEEDED    ("stop execution when number of steps exceeds the execution limit",false, true),

    ERR_INVALID_COUNTER             ("stop execution when an invalid value is written to '@counter'"),
    ERR_INVALID_IDENTIFIER          ("stop execution when a malformed identifier or value is encountered"),
    ERR_UNSUPPORTED_OPCODE          ("stop execution when unsupported instruction is encountered"),
    ERR_UNINITIALIZED_VAR           ("stop execution when an uninitialized variable is used in internal processing", true, false),
    ERR_ASSIGNMENT_TO_FIXED_VAR     ("stop execution on attempts to write a value to an unmodifiable built-in variable"),
    ERR_NOT_AN_OBJECT               ("stop execution when a numeric value is used instead of an object"),
    ERR_NOT_A_NUMBER                ("stop execution when an object is used instead of a numeric value"),
    ERR_INVALID_CONTENT             ("stop execution when an invalid index is used in the 'lookup' instruction"),
    ERR_INVALID_LINK                ("stop execution when an invalid index is used in the 'getlink' instruction"),
    ERR_MEMORY_ACCESS               ("stop execution when accessing invalid memory-cell or memory-bank index "),
    ERR_UNSUPPORTED_BLOCK_OPERATION ("stop execution when attempting to perform an unsupported operation on a block"),
    ERR_TEXT_BUFFER_OVERFLOW        ("stop execution when the text buffer size (400 characters) is exceeded", true, false),
    ERR_INVALID_FORMAT              ("stop execution when no placeholder for the 'format' instruction exists in the buffer"),
    ERR_GRAPHICS_BUFFER_OVERFLOW    ("stop execution when the graphics buffer size (256 operations) is exceeded"),

    ERR_INVALID_ASSERT_PRINTS       ("stop execution when 'assertflush' and 'assertprints' are called out of order",false, true),
    ;

    private final String description;
    private final boolean settable;
    private final boolean active;
    private final String optionName;

    ExecutionFlag(String description, boolean settable, boolean active) {
        if (!settable && !active) {
            throw new IllegalArgumentException("always inactive flag");
        }
        this.description = description;
        this.settable = settable;
        this.active = active;
        this.optionName = name().toLowerCase().replace('_', '-');
    }

    ExecutionFlag(String description) {
        this(description, true, true);
    }

    public String getDescription() {
        return description;
    }

    public String getOptionName() {
        return optionName;
    }

    public boolean isSettable() {
        return settable;
    }

    public static final List<ExecutionFlag> LIST = List.of(values());

    public static EnumSet<ExecutionFlag> getDefaultFlags() {
        EnumSet<ExecutionFlag> flags = EnumSet.allOf(ExecutionFlag.class);
        LIST.stream().filter(f -> !f.active).forEach(flags::remove);
        return flags;
    }
}
