package info.teksol.mc.mindcode.compiler.ast.nodes;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum AstNodeScope {
    GLOBAL,
    LOCAL,
    NONE,
    ;

    public boolean disallowed(AstNodeScope currentScope) {
        return this != NONE && this != currentScope;
    }
}
