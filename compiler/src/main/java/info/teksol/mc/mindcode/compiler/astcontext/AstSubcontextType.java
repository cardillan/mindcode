package info.teksol.mc.mindcode.compiler.astcontext;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum AstSubcontextType {
    /// Nonspecific context.
    BASIC           ("    "),

    /// Mocked context. Used by testing code.
    MOCK            ("    "),

    /// System-generated END instruction
    END             ("END "),

    /// Sequence of statements, with a single entry point and (mostly) single exit point.
    /// Break, continue and return might jump out of the block, use `BaseOptimizer.isContained`
    /// to detect these cases.
    BODY            ("BODY"),

    /// The else branch of a case statement. Like a body, but must be handled specifically by expression optimizers.
    ELSE            ("ELSE"),

    /// The condition in if, loop, when or similar structures. Contains code for the entire expression.
    CONDITION       ("COND"),

    /// Jumps between blocks, or labels for such jumps.
    FLOW_CONTROL    ("FLOW"),

    /// Initialization code for a control structure (only loops at this moment).
    INIT            ("INIT"),

    /// Update code (setting up next iteration) in a loop.
    UPDATE          ("UPDT"),

    /// Part of list iterator loop executed before each iteration
    ITR_LEADING     ("ITRL"),

    /// Part of list iterator loop executed after each iteration
    ITR_TRAILING    ("ITRT"),

    /// Code setting up arguments before a function call.
    ARGUMENTS       ("ARGS"),

    /// Code copying arguments to parameters before a function call.
    PARAMETERS      ("PRMS"),

    /// Code copying return value(s) of a function call to temporary variables.
    RETURN_VALUE    ("RETV"),

    /// Call to a system function (mapped to an instruction) or a built-in function.
    /// Technically not a call, as it includes the entire function body.
    SYSTEM_CALL     ("SCAL"),

    INLINE_CALL     ("ICAL"),

    ///  Call to an out-of-line (but stackless) function.
    OUT_OF_LINE_CALL("OCAL"),

    /// A recursive function call, includes stack operations.
    RECURSIVE_CALL  ("RCAL"),

    /// A remote function call.
    REMOTE_CALL     ("RMCL"),

    /// Initialization of a remote module.
    REMOTE_INIT     ("RMIN"),

    /// Array access
    ARRAY           ("ARRA"),

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
