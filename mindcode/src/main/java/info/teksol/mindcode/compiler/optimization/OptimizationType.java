package info.teksol.mindcode.compiler.optimization;

public enum OptimizationType {
    /** Optimization changes a function call from stackless to inline. */
    INLINE,

    /** Optimization duplicates existing code to avoid control flow jumps. */
    DUPLICATE,

    /** Optimization replaces a series of instructions with alternative arrangement. Code won't be duplicated. */
    REPLACE,
}
