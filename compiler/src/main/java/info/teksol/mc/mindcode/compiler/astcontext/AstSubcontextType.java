package info.teksol.mc.mindcode.compiler.astcontext;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum AstSubcontextType {
    /// Nonspecific context.
    BASIC           ("    "),

    /// Mocked context. Used by testing code.
    MOCK            ("    "),

    /// System-generated END instruction
    END             ("END ", false),

    /// Sequence of statements, with a single entry point and (mostly) single exit point.
    /// Break, continue and return might jump out of the block, use `BaseOptimizer.isContained`
    /// to detect these cases.
    BODY            ("BODY"),

    /// The else branch of a case statement. Like a body, but must be handled specifically by expression optimizers.
    ELSE            ("ELSE"),

    /// The condition in if, loop, when or similar structures. Contains code for the entire expression.
    CONDITION       ("COND", false),

    /// Jumps between blocks, or labels for such jumps.
    FLOW_CONTROL    ("FLOW", false),

    /// Initialization code for a control structure (only loops at this moment).
    INIT            ("INIT"),

    /// Hoisting context withing a fully rotated loop (placed between the initial condition and the loop body)
    HOIST           ("HOIS"),

    /// Update code (setting up the next iteration) in a loop.
    UPDATE          ("UPDT", false),

    /// Part of list iterator loop executed before each iteration
    ITR_LEADING     ("ITRL", false),

    /// Part of list iterator loop executed after each iteration
    ITR_TRAILING    ("ITRT", false),

    /// Code setting up arguments before a function call.
    ARGUMENTS       ("ARGS"),

    /// Code copying arguments to parameters before a function call.
    PARAMETERS      ("PRMS"),

    /// Code copying return value(s) of a function call to temporary variables.
    RETURN_VALUE    ("RETV", false),

    /// A call to a system function (mapped to an instruction) or a built-in function.
    /// Technically not a call, as it includes the entire function body.
    SYSTEM_CALL     ("SCAL"),

    INLINE_CALL     ("ICAL"),

    ///  A call to an out-of-line (but stackless) function.
    OUT_OF_LINE_CALL("OCAL", false),

    /// A recursive function call, includes stack operations.
    RECURSIVE_CALL  ("RCAL", false),

    /// A remote function call.
    REMOTE_CALL     ("RMCL", false),

    /// Initialization of a remote module.
    REMOTE_INIT     ("RMIN", false),

    /// Array access
    ARRAY           ("ARRA"),

    /// Relocation context
    RELOCATION      ("RELO"),

    ;

    public final String text;
    public final boolean safe;

    AstSubcontextType(String text) {
        this.text = text;
        this.safe = true;
    }

    AstSubcontextType(String text, boolean safe) {
        this.text = text;
        this.safe = safe;
    }
}
