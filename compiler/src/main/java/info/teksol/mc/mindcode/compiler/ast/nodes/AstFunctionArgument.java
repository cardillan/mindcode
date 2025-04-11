package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode
public class AstFunctionArgument extends AstFragment {
    private final @Nullable AstExpression expression;
    private final boolean inModifier;
    private final boolean outModifier;
    private final boolean refModifier;

    public AstFunctionArgument(SourcePosition sourcePosition, AstExpression expression,
            boolean inModifier, boolean outModifier, boolean refModifier) {
        super(sourcePosition, children(expression));
        this.expression = Objects.requireNonNull(expression);
        this.inModifier = inModifier;
        this.outModifier = outModifier;
        this.refModifier = refModifier;
    }

    public AstFunctionArgument(SourcePosition sourcePosition) {
        super(sourcePosition);
        this.expression = null;
        this.inModifier = false;
        this.outModifier = false;
        this.refModifier = false;
    }

    public @Nullable AstExpression getExpression() {
        return expression;
    }

    public boolean hasExpression() {
        return expression != null;
    }

    public boolean hasModifier() {
        return inModifier || outModifier || refModifier;
    }

    public boolean hasInModifier() {
        return inModifier;
    }

    public boolean hasOutModifier() {
        return outModifier;
    }

    public boolean hasRefModifier() {
        return refModifier;
    }

    public boolean isInput() {
        return inModifier || !outModifier;
    }

    public boolean isOutput() {
        return outModifier;
    }

    public boolean isReference() {
        return refModifier;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstFunctionArgument that = (AstFunctionArgument) o;
        return inModifier == that.inModifier && outModifier == that.outModifier && refModifier == that.refModifier
                && Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(expression);
        result = 31 * result + Boolean.hashCode(inModifier);
        result = 31 * result + Boolean.hashCode(outModifier);
        result = 31 * result + Boolean.hashCode(refModifier);
        return result;
    }

}
