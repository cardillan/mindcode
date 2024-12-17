package info.teksol.mindcode.v3;

public enum CompilationPhase {
    LEXER,
    PARSER,
    AST_BUILDER,
    COMPILER,
    OPTIMIZER,
    ALL;

    public boolean includes(CompilationPhase phase) {
        return this.ordinal() >= phase.ordinal();
    }
}
