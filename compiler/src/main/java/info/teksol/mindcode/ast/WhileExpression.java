package info.teksol.mindcode.ast;

import java.util.Objects;

public class WhileExpression implements AstNode {
    private final AstNode condition;
    private final AstNode body;

    WhileExpression(AstNode condition, AstNode body) {
        this.condition = condition;
        this.body = body;
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
        WhileExpression that = (WhileExpression) o;
        return Objects.equals(condition, that.condition) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, body);
    }

    @Override
    public String toString() {
        return "WhileExpression{" +
                "condition=" + condition +
                ", body=" + body +
                '}';
    }
}
