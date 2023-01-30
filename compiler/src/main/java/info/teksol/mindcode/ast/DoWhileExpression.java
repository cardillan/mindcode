package info.teksol.mindcode.ast;

import java.util.Objects;

public class DoWhileExpression implements AstNode {
    private final String label;
    private final AstNode body;
    private final AstNode condition;

    DoWhileExpression(AstNode body, AstNode condition) {
        this(null, body, condition);
    }

    DoWhileExpression(String label, AstNode body, AstNode condition) {
        this.label = label;
        this.body = body;
        this.condition = condition;
    }

    public String getLabel() {
        return label;
    }

    public AstNode getCondition() {
        return condition;
    }

    public AstNode getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoWhileExpression that = (DoWhileExpression) o;
        return Objects.equals(label, that.label) && Objects.equals(body, that.body) &&
                Objects.equals(condition, that.condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, body);
    }

    @Override
    public String toString() {
        return "DoWhileExpression{" +
                "label=" + label +
                ", body=" + body +
                ", condition=" + condition +
                '}';
    }
}
