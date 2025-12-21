package info.teksol.mc.mindcode.compiler.astcontext;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum AstContextType {
    NONE            ("NONE"),
    ROOT            ("ROOT"),
    INIT            ("INIT"),
    CODE            ("CODE"),
    MLOG            ("MLOG", true),
    FUNCTION_DEF    ("FNDF"),
    FUNCTION_BODY   ("FUNC"),
    CALL            ("CALL", true, true),
    RETURN          ("RETN", true),

    IF              ("IF  ", true),
    CASE            ("CASE", true),
    LOOP            ("LOOP", true),
    EACH            ("EACH", true),
    BREAK           ("BREA", true),
    CONTINUE        ("CONT", true),
    SCBE_COND       ("SCIF", true),
    SCBE_OPER       ("SCOP", true),

    // Non-control flow
    ASSIGNMENT      ("ASGN"),
    OPERATOR        ("OPER"),
    PROPERTY        ("PROP"),
    ARRAY_ACCESS    ("HEAP"),
    ALLOCATION      ("ALOC"),

    // A jump table. Not to be optimized.
    // The jump table effects are expressed as side effects of the first instruction.
    JUMPS           ("JUMP", true),

    // Variable creation context - added in label resolving phase, never optimized
    CREATE_VARS     ("CVAR"),
    ;

    public final String text;
    public final boolean flowControl;
    public final boolean safe;

    AstContextType(String text) {
        this.text = text;
        this.flowControl = false;
        this.safe = true;
    }

    AstContextType(String text, boolean flowControl) {
        this.text = text;
        this.flowControl = flowControl;
        this.safe = false;
    }

    AstContextType(String text, boolean flowControl, boolean safe) {
        this.text = text;
        this.flowControl = flowControl;
        this.safe = safe;
    }
}
