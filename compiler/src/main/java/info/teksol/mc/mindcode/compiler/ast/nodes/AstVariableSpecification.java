package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
@AstNode
public class AstVariableSpecification extends AstFragment {
    private final AstIdentifier identifier;
    private final boolean array;
    private final @Nullable AstExpression arraySize;
    private final List<AstExpression> expressions;

    public AstVariableSpecification(SourcePosition sourcePosition, AstIdentifier identifier, @Nullable AstExpression expression) {
        super(sourcePosition, children(identifier, expression));
        this.identifier = identifier;
        this.array = false;
        this.arraySize = null;
        this.expressions = expression == null ? List.of() : List.of(expression);
    }

    public AstVariableSpecification(SourcePosition sourcePosition, AstIdentifier identifier, boolean array,
            @Nullable AstExpression arraySize, List<AstExpression> expressions) {
        super(sourcePosition, children(list(identifier, arraySize), expressions));
        this.identifier = identifier;
        this.array = array;
        this.arraySize = arraySize;
        this.expressions = expressions;
    }

    public AstIdentifier getIdentifier() {
        return identifier;
    }

    public boolean isArray() {
        return array;
    }

    public @Nullable AstExpression getArraySize() {
        return arraySize;
    }

    public List<AstExpression> getExpressions() {
        return expressions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstVariableSpecification that = (AstVariableSpecification) o;
        return array == that.array && identifier.equals(that.identifier) && Objects.equals(arraySize, that.arraySize) && expressions.equals(that.expressions);
    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + Boolean.hashCode(array);
        result = 31 * result + Objects.hashCode(arraySize);
        result = 31 * result + expressions.hashCode();
        return result;
    }
}
