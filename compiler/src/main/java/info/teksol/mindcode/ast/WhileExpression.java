package info.teksol.mindcode.ast;

import java.util.Objects;

public class WhileExpression implements AstNode {
    private final AstNode condition;
    private final AstNode body;
    private final AstNode update;

    WhileExpression(AstNode condition, AstNode body, AstNode update) {
        this.condition = condition;
        this.body = body;
        this.update = update;
    }

    public AstNode getCondition() {
        return condition;
    }

    public AstNode getBody() {
        return body;
    }

    public AstNode getUpdate() {
        return update;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WhileExpression that = (WhileExpression) o;
        return Objects.equals(condition, that.condition) &&
                Objects.equals(body, that.body) && Objects.equals(update, that.update);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, body, update);
    }

    @Override
    public String toString() {
        return "WhileExpression{" +
                "condition=" + condition +
                ", body=" + body +
                ", update=" + update +
                '}';
    }
}
