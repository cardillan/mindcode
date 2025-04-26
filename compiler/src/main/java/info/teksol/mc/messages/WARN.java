package info.teksol.mc.messages;

public class WARN {
    public static final String ABSOLUTE_ADDRESSING = "Unable to meet the requirement for symbolic links. Some portions of the program still depend on absolute addressing.";
    public static final String BUILT_IN_VARIABLE_NOT_RECOGNIZED = "Built-in variable '%s' not recognized.";
    public static final String FORMAT_PRECLUDED_BY_STRING_LITERAL = "A string literal precludes using 'format' instruction for print merging.";
    public static final String FUNCTION_NO_LONGER_SUPPORTED = "Function '%s' is no longer supported in Mindustry Logic version %s; using '%s' instead.";
    public static final String LINKED_VARIABLE_NOT_RECOGNIZED = "Linked variable name '%s' doesn't correspond to any known linked block name.";
    public static final String LITERAL_LOSS_OF_PRECISION = "Loss of precision while creating mlog literal (original value %s, encoded value %s).";
    public static final String LITERAL_UNSAFE_DECIMAL_RANGE = "Literal '%s' exceeds safe range for integer operations (0 .. 2**52).";
    public static final String LOOP_KEYWORD_DEPRECATED = "The 'loop' keyword is deprecated. Use 'while' instead of 'loop while'.";
    public static final String MISSING_MLOG_KEYWORD_PREFIX = "Using mlog keywords without the ':' prefix is deprecated (use ':%s' instead).";
    public static final String MISSING_PRINTFLUSH_ADDED = "Automatically added missing 'printflush(message1);' call to the end of the program.";
    public static final String NAMED_COLOR_NOT_RECOGNIZED = "Named color '%s' not recognized.";
    public static final String OPTIMIZATION_PASSES_LIMIT_REACHED = "Optimization passes limit (%d) reached.";
    public static final String PRINTF_NOT_ENOUGH_ARGUMENTS = "The 'printf' function doesn't have enough arguments for placeholders: %d placeholder(s), %d argument(s).";
    public static final String PRINTF_NO_PLACEHOLDERS = "The 'printf' function is called with a literal format string which doesn't contain any format placeholders.";
    public static final String PRINTF_TOO_MANY_ARGUMENTS = "The 'printf' function has more arguments than placeholders: %d placeholder(s), %d argument(s).";
    public static final String PRINTF_WITH_LITERAL_FORMAT = "The 'printf' function is called with a literal format string. Using 'print' or 'println' with formattable string literals instead may produce better code.";
    public static final String VARIABLE_NOT_DEFINED = "Variable '%s' is not defined.";
    public static final String VARIABLE_NOT_INITIALIZED = "Variable '%s' is not initialized.";
    public static final String VARIABLE_NOT_USED = "Variable '%s' is not used.";
    public static final String VOLATILE_VARIABLE_NOT_USED = "Volatile variable '%s' is unused.";
    public static final String VOID_EXPRESSION_DEPRECATED = "Expression doesn't have any value. Using value-less expressions in assignments is deprecated.";
    public static final String VOID_RETURN = "Expression doesn't have any value. Using value-less expressions in return statements is deprecated.";
}
