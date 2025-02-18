package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.Modifier;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

// Modifiers are represented as AST nodes so that we can track their source position
@NullMarked
@AstNode
public class AstVariableModifier extends AstFragment {
    private final Modifier modifier;
    private final @Nullable AstMindcodeNode parametrization;

    public AstVariableModifier(SourcePosition sourcePosition, Modifier modifier, @Nullable AstMindcodeNode parametrization) {
        super(sourcePosition, children(parametrization));
        this.modifier = modifier;
        this.parametrization = parametrization;
    }

    public Modifier getModifier() {
        return modifier;
    }

    public @Nullable AstMindcodeNode getParametrization() {
        return parametrization;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstVariableModifier that = (AstVariableModifier) o;
        return modifier == that.modifier && Objects.equals(parametrization, that.parametrization);
    }

    @Override
    public int hashCode() {
        int result = modifier.hashCode();
        result = 31 * result + Objects.hashCode(parametrization);
        return result;
    }
}
