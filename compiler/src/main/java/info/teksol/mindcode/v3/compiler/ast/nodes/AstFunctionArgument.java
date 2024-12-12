package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
@AstNode
public class AstFunctionArgument extends AstFragment {
    private final @Nullable AstExpression expression;
    private final boolean inModifier;
    private final boolean outModifier;

    public AstFunctionArgument(InputPosition inputPosition, AstExpression expression,
            boolean inModifier, boolean outModifier) {
        super(inputPosition);
        this.expression = Objects.requireNonNull(expression);
        this.inModifier = inModifier;
        this.outModifier = outModifier;
    }

    public AstFunctionArgument(InputPosition inputPosition) {
        super(inputPosition);
        this.expression = null;
        this.inModifier = false;
        this.outModifier = false;
    }

    public @Nullable AstExpression getExpression() {
        return expression;
    }

    public boolean hasExpression() {
        return expression != null;
    }

    public boolean hasModifier() {
        return inModifier || outModifier;
    }

    public boolean hasInModifier() {
        return inModifier;
    }

    public boolean hasOutModifier() {
        return outModifier;
    }

    public boolean isInput() {
        return inModifier || !outModifier;
    }

    public boolean isOutput() {
        return outModifier;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstFunctionArgument that = (AstFunctionArgument) o;
        return inModifier == that.inModifier && outModifier == that.outModifier && Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(expression);
        result = 31 * result + Boolean.hashCode(inModifier);
        result = 31 * result + Boolean.hashCode(outModifier);
        return result;
    }

    @Override
    public String toString() {
        return "AstFunctionArgument{" +
               "expression=" + expression +
               ", inModifier=" + inModifier +
               ", outModifier=" + outModifier +
               '}';
    }
}
