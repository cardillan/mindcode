package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.FunctionModifier;
import org.jspecify.annotations.NullMarked;

// Modifiers are represented as AST nodes so that we can track their source position
@NullMarked
@AstNode
public class AstFunctionModifier extends AstFragment {
    private final FunctionModifier modifier;

    public AstFunctionModifier(SourcePosition sourcePosition, FunctionModifier modifier) {
        super(sourcePosition);
        this.modifier = modifier;
    }

    public FunctionModifier getModifier() {
        return modifier;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstFunctionModifier that = (AstFunctionModifier) o;
        return modifier == that.modifier;
    }

    @Override
    public int hashCode() {
        return modifier.hashCode();
    }
}
