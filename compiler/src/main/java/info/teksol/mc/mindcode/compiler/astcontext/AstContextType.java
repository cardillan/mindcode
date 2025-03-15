package info.teksol.mc.mindcode.compiler.astcontext;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum AstContextType {
    NONE            ("NONE"),
    ROOT            ("ROOT"),
    INIT            ("INIT"),
    BODY            ("BODY"),
    FUNCTION        ("FNDF"),
    CALL            ("CALL", true),
    RETURN          ("RETN", true),

    IF              ("IF  ", true),
    CASE            ("CASE", true),
    LOOP            ("LOOP", true),
    EACH            ("EACH", true),
    BREAK           ("BREA", true),
    CONTINUE        ("CONT", true),

    // Non-control flow
    ASSIGNMENT      ("ASGN"),
    OPERATOR        ("OPER"),
    PROPERTY        ("PROP"),
    ARRAY_ACCESS    ("HEAP"),
    ALLOCATION      ("ALOC"),

    ARRAY_INIT      ("IARR"),
    ARRAY_READ      ("RARR"),
    ARRAY_WRITE     ("WARR"),
    ;

    public final String text;
    public final boolean flowControl;

    AstContextType(String text) {
        this.text = text;
        flowControl = false;
    }

    AstContextType(String text, boolean flowControl) {
        this.text = text;
        this.flowControl = flowControl;
    }
}
