package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AstFunctionArgument extends AstBaseMindcodeNode {
    private final AstMindcodeNode expression;
    private final boolean inModifier;
    private final boolean outModifier;

    public AstFunctionArgument(InputPosition inputPosition, @NotNull AstMindcodeNode expression, boolean inModifier, boolean outModifier) {
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

    public AstMindcodeNode getExpression() {
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
