package info.teksol.mindcode.logic;

public enum ArgumentType {
    // Represents a nonexistent value (value of a void function or a loop)
    VOID,

    // Literals
    NULL_LITERAL,
    BOOLEAN_LITERAL,
    NUMERIC_LITERAL,
    STRING_LITERAL,

    // Instruction keyword
    KEYWORD,

    // Mindustry built-in constant or variable, such as @coal, @time or @unit
    BUILT_IN,

    // Labels
    LABEL,

    // User variables
    BLOCK,
    PARAMETER,                      // Program parameter
    GLOBAL_VARIABLE,
    LOCAL_VARIABLE,

    // Compiler generated variables
    COMPILER,                       // Compiler support, e.g. stack pointer(s)

    TMP_VARIABLE,
    AST_VARIABLE,
    FUNCTION_RETVAL,
    FUNCTION_RETADDR,

    // No information about type - for creating instructions without metadata
    UNSPECIFIED,
    ;

    public boolean isCompiler() {
        return ordinal() >= COMPILER.ordinal();
    }
}
