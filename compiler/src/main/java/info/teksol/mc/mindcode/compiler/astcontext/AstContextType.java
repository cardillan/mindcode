package info.teksol.mc.mindcode.compiler.astcontext;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum AstContextType {
    NONE            ("NONE"),
    ROOT            ("ROOT"),
    INIT            ("INIT"),
    CODE            ("CODE"),
    MLOG            ("MLOG"),
    FUNCTION_DEF    ("FNDF"),
    FUNCTION_BODY   ("FUNC"),
    CALL            ("CALL", true),
    RETURN          ("RETN", true),

    IF              ("IF  ", true),
    CASE            ("CASE", true),
    LOOP            ("LOOP", true),
    EACH            ("EACH", true),
    BREAK           ("BREA", true),
    CONTINUE        ("CONT", true),
    SHORT_CIRCUIT   ("SHCT", true),

    // Non-control flow
    ASSIGNMENT      ("ASGN"),
    OPERATOR        ("OPER"),
    PROPERTY        ("PROP"),
    ARRAY_ACCESS    ("HEAP"),
    ALLOCATION      ("ALOC"),

    // A jump table. Not to be optimized.
    // The jump table effects are expressed as side effects of the first instruction.
    JUMPS           ("JUMP"),

    // Variable creation context - added in label resolving phase, never optimized
    CREATE_VARS     ("CVAR"),
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
