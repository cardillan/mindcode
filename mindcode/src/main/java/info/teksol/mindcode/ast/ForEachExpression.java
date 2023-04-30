package info.teksol.mindcode.ast;

import java.util.List;
import java.util.Objects;

public class ForEachExpression extends ControlBlockAstNode {
    private final String label;
    private final AstNode variable;
    private final List<AstNode> values;
    private final AstNode body;

    ForEachExpression(String label, AstNode variable, List<AstNode> values, AstNode body) {
        super(values, variable, body);
        this.label = label;
        this.variable = variable;
        this.values = values;
        this.body = body;
    }

    public String getLabel() {
        return label;
    }

    public AstNode getVariable() {
        return variable;
    }

    public List<AstNode> getValues() {
        return values;
    }

    public AstNode getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ForEachExpression forEachExpression = (ForEachExpression) o;
        return Objects.equals(label, forEachExpression.label) &&
                Objects.equals(variable, forEachExpression.variable) &&
                Objects.equals(values, forEachExpression.values) &&
                Objects.equals(body, forEachExpression.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, variable, values, body);
    }

    @Override
    public String toString() {
        return "ForEachExpression{" +
                "label=" + label +
                ", variable='" + variable + '\'' +
                ", values=" + values +
                ", body=" + body +
                '}';
    }
}
