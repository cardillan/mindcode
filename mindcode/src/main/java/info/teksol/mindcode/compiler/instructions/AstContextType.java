package info.teksol.mindcode.compiler.instructions;

public enum AstContextType {
    NONE            ("NONE"),
    ROOT            ("ROOT"),
    BODY            ("BODY"),
    FUNCTION        ("FNDF"),
    CALL            ("CALL"),
    RETURN          ("RETN"),

    IF              ("IF  ", true),
    CASE            ("CASE", true),
    LOOP            ("LOOP", true),
    BREAK           ("BREA"),
    CONTINUE        ("CONT"),

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
