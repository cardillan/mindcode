package info.teksol.mindcode.logic;

public enum ArgumentType {
    // Instruction keyword
    KEYWORD,

    // Mindustry built-in constant or variable, such as @coal, @time or @unit
    BUILT_IN,

    // Labels
    LABEL,

    // Variables
    BLOCK,
    GLOBAL_VARIABLE,
    LOCAL_VARIABLE,
    TMP_VARIABLE,
    AST_VARIABLE,
    FUNCTION_RETVAL,
    FUNCTION_RETADDR,
    COMPILER,                // Compiler variables, e.g. stack pointer(s)

    // Literals
    NULL_LITERAL,
    BOOLEAN_LITERAL,
    NUMERIC_LITERAL,
    STRING_LITERAL,

    // No information about type - for creating instructions without metadata
    UNSPECIFIED,
}
