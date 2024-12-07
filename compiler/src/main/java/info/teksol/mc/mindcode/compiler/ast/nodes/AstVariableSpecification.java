package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode
public class AstVariableSpecification extends AstFragment {
    private final AstIdentifier identifier;
    private final @Nullable AstExpression expression;

    public AstVariableSpecification(SourcePosition sourcePosition, AstIdentifier identifier, @Nullable AstExpression expression) {
        super(sourcePosition, children(identifier, expression));
        this.identifier = identifier;
        this.expression = expression;
    }

    public AstIdentifier getIdentifier() {
        return identifier;
    }

    public @Nullable AstExpression getExpression() {
        return expression;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstVariableSpecification that = (AstVariableSpecification) o;
        return identifier.equals(that.identifier) && Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + Objects.hashCode(expression);
        return result;
    }
}
