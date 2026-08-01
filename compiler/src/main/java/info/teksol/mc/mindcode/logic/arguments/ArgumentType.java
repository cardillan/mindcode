package info.teksol.mc.mindcode.logic.arguments;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum ArgumentType {
    // Represents a nonexistent value (value of a void function or a loop)
    VOID,

    // Literals
    NULL_LITERAL,
    COLOR_LITERAL,
    NAMED_COLOR_LITERAL,
    BOOLEAN_LITERAL,
    NUMERIC_LITERAL,
    STRING_LITERAL,

    // Instruction keyword
    KEYWORD,

    // Mindustry built-in constant or variable, such as @coal, @time or @unit
    BUILT_IN,

    // Labels
    LABEL,

    // Arrays
    ARRAY,

    // User variables

    BLOCK,
    PROGRAM_PARAMETER,
    GLOBAL_VARIABLE,                // Also array elements
    FUNCTION_PARAMETER,
    LOCAL_VARIABLE,

    MLOG_VARIABLE,                  // Created from an mlog expression

    // Compiler variables

    ADDRESS,                        // For storing addresses. Is always initialized, may be eliminated when unused.

    GLOBAL_PRESERVED,               // A global variable, which must not be eliminated
    PRESERVED,                      // Must not be eliminated

    TMP_VARIABLE,
    AST_VARIABLE,
    FUNCTION_RETVAL,
    FUNCTION_RETADDR,
    FUNCTION_STACKFRAME,

    // No information about type - for creating instructions without metadata
    UNSPECIFIED,
    ;

    public boolean reportUninitialized() {
        return ordinal() >= GLOBAL_PRESERVED.ordinal() && this != FUNCTION_RETVAL;
    }
}
