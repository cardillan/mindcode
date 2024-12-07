package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.Modifier;
import org.jspecify.annotations.NullMarked;

// Modifiers are represented as AST nodes so that we can track their source position
@NullMarked
@AstNode
public class AstVariableModifier extends AstFragment {
    private final Modifier modifier;

    public AstVariableModifier(SourcePosition sourcePosition, Modifier modifier) {
        super(sourcePosition);
        this.modifier = modifier;
    }

    public Modifier getModifier() {
        return modifier;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstVariableModifier that = (AstVariableModifier) o;
        return modifier == that.modifier;
    }

    @Override
    public int hashCode() {
        return modifier.hashCode();
    }
}
