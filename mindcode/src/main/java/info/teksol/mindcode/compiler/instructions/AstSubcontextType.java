package info.teksol.mindcode.compiler.instructions;

public enum AstSubcontextType {
    BASIC           ("    "),

    WHEN_VALUES     ("WVAL"),
    WHEN_BODY       ("WBOD"),

    CONDITION       ("ICON"),

    /** Unconditional transition between two blocks, or labels taking parts in such transitions. */
    FLOW_CONTROL    ("FLOW"),


    BODY            ("BODY"),
    LOOP_INIT       ("LCON"),
    LOOP_CONDITION  ("LCON"),
    LOOP_UPDATE     ("LUPD"),
    LOOP_ITERATOR   ("ITER"),

    FUNCTION_ARGUMENTS  ("ARGS"),
    SYSTEM_FUNCTION     ("SFUN"),
    INLINE_FUNCTION     ("INLI"),
    OUT_OF_LINE_CALL    ("OCAL"),
    RECURSIVE_CALL      ("RCAL"),

    ;

    public final String text;
    final double multiplier;

    AstSubcontextType(String text) {
        this.text = text;
        this.multiplier = 1.0;
    }

    AstSubcontextType(String text, double multiplier) {
        this.text = text;
        this.multiplier = multiplier;
    }
}
