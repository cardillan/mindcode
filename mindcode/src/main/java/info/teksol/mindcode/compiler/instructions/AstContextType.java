package info.teksol.mindcode.compiler.instructions;

public enum AstContextType {
    NONE            ("NONE"),
    ROOT            ("ROOT"),
    HEAP_ALLOC      ("HALO"),
    HEAP_ACCESS     ("HACS"),
    ASSIGNMENT      ("ASGN"),
    BINARY_OP       ("BINO"),
    BOOL_BINARY_OP  ("BINB"),
    UNARY_OP        ("UNOP"),
    STACK_ALLOC     ("SALO"),

    FUNCTION        ("FNDF"),
    CALL            ("CALL"),
    INLINED_CALL    ("ICAL"),
    RETURN          ("RETN"),

    IF              ("IF  "),
    CASE            ("CASE"),
    WHEN            ("WHEN"),

    LOOP            ("LOOP"),
    BREAK           ("BREA"),
    CONTINUE        ("CONT"),
    PROPERTY        ("PROP"),
    CONTROL         ("CTRL"),
    ;

    public final String text;

    AstContextType(String text) {
        this.text = text;
    }
}
