package info.teksol.mc.mindcode.compiler;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum CompilationPhase {
    LEXER,
    PARSER,
    AST_BUILDER,
    CALL_GRAPH,
    COMPILER,
    OPTIMIZER,
    PRINTER,
    EMULATOR,
    ALL;

    public boolean includes(CompilationPhase phase) {
        return this.ordinal() >= phase.ordinal();
    }
}
