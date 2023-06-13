package info.teksol.mindcode.compiler.instructions;

public enum AstContextType {
    NONE            ("NONE"),
    ROOT            ("ROOT"),
    FUNCTION        ("FNDF"),
    CALL            ("CALL"),
    RETURN          ("RETN"),

    IF              ("IF  "),
    CASE            ("CASE"),
    LOOP            ("LOOP"),
    BREAK           ("BREA"),
    CONTINUE        ("CONT"),
    ;

    public final String text;

    AstContextType(String text) {
        this.text = text;
    }
}
