package info.teksol.mindcode.compiler.generator;

public enum AstContextType {
    NONE            ("NONE"),
    ROOT            ("ROOT"),
    BODY            ("BODY"),
    FUNCTION        ("FNDF"),
    CALL            ("CALL", true),
    RETURN          ("RETN", true),

    IF              ("IF  ", true),
    CASE            ("CASE", true),
    LOOP            ("LOOP", true),
    BREAK           ("BREA", true),
    CONTINUE        ("CONT", true),

    // Non-control flow
    ASSIGNMENT      ("ASGN"),
    OPERATOR        ("OPER"),
    PROPERTY        ("PROP"),
    HEAP_ACCESS     ("HEAP"),
    ALLOCATION      ("ALOC"),
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
