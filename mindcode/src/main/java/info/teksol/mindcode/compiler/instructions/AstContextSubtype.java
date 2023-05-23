package info.teksol.mindcode.compiler.instructions;

public enum AstContextSubtype {
    BASIC           ("    "),

    WHEN_VALUES     ("WVAL"),
    WHEN_BODY       ("WBOD"),

    IF_CONDITION    ("ICON"),
    TRUE_BRANCH     ("TBRN"),
    FALSE_BRANCH    ("FBRN"),


    BODY            ("BODY"),
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

    AstContextSubtype(String text) {
        this.text = text;
        this.multiplier = 1.0;
    }

    AstContextSubtype(String text, double multiplier) {
        this.text = text;
        this.multiplier = multiplier;
    }
}
