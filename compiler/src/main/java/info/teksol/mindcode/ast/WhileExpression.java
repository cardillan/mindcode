package info.teksol.mindcode.ast;

import java.util.Objects;

public class WhileExpression extends BaseAstNode {
    private final String label;
    private final AstNode condition;
    private final AstNode body;
    private final AstNode update;

    WhileExpression(AstNode condition, AstNode body, AstNode update) {
        this(null, condition, body, update);
    }

    WhileExpression(String label, AstNode condition, AstNode body, AstNode update) {
        super(condition, body);
        this.label = label;
        this.condition = condition;
        this.body = body;
        this.update = update;
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

    public AstNode getUpdate() {
        return update;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WhileExpression that = (WhileExpression) o;
        return Objects.equals(label, that.label) && Objects.equals(condition, that.condition) &&
                Objects.equals(body, that.body) && Objects.equals(update, that.update);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, condition, body, update);
    }

    @Override
    public String toString() {
        return "WhileExpression{" +
                "label=" + label +
                ", condition=" + condition +
                ", body=" + body +
                ", update=" + update +
                '}';
    }
}
