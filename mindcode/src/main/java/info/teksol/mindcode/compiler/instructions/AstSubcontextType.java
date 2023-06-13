package info.teksol.mindcode.compiler.instructions;

public enum AstSubcontextType {
    /** Nonspecific context. */
    BASIC           ("    "),

    /** System generated END instruction */
    END             ("END "),

    /**
     * Sequence of statements, with a single entry point and (mostly) single exit point.
     * Break, continue and return might jump out of the block, use {@code BaseOptimizer.isContained}
     * to detect these cases.
     */
    BODY            ("BODY"),

    /** The else branch of a case statement. Like a body, but must be handled specifically by expression optimizers. */
    ELSE            ("ELSE"),

    /** The condition in if, loop, when or similar structures. Contains code for the entire expression. */
    CONDITION       ("COND"),

    /** Jumps between blocks, or labels for such jumps. */
    FLOW_CONTROL    ("FLOW"),

    /** Initialization code for a control structure (only loops at this moment).  */
    INIT            ("INIT"),

    /** Update code (setting up next iteration) in a loop. */
    UPDATE          ("UPDT"),

    /** For-each iterator code. No further structure at the moment. */
    ITERATOR        ("ITER"),

    /** Code setting up arguments before a function call. */
    ARGUMENTS       ("ARGS"),

    /**
     * Call to a system function (mapped to an instruction) or a built-in function.
     * Technically not a call, as it includes the entire function body.
     */
    SYSTEM_CALL     ("SCAL"),

    INLINE_CALL     ("ICAL"),

    /**  Call to an out-of-line (but stackless) function. */
    OUT_OF_LINE_CALL("OCAL"),

    /** A recursive function call, includes stack operations. */
    RECURSIVE_CALL  ("RCAL"),

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
