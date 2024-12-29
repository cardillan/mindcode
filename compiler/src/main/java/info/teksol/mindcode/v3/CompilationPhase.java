package info.teksol.mindcode.v3;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum CompilationPhase {
    LEXER,
    PARSER,
    AST_BUILDER,
    CALL_GRAPH,
    COMPILER,
    OPTIMIZER,
    EMULATOR,
    ALL;

    public boolean includes(CompilationPhase phase) {
        return this.ordinal() >= phase.ordinal();
    }
}
