package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;

import java.util.List;
import java.util.Objects;

public class AstExpressionList extends AstBaseMindcodeNode {
    private final List<AstMindcodeNode> expressions;

    public AstExpressionList(InputPosition inputPosition, List<AstMindcodeNode> expressions) {
        super(inputPosition);
        this.expressions = Objects.requireNonNull(expressions);
    }

    public List<AstMindcodeNode> getExpressions() {
        return expressions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstExpressionList that = (AstExpressionList) o;
        return Objects.equals(expressions, that.expressions);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(expressions);
    }

    @Override
    public String toString() {
        return "AstExpressionList{" +
                "expressions=" + expressions +
                '}';
    }
}
