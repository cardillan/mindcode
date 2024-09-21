package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.generator.AstContextType;
import org.antlr.v4.runtime.Token;

import java.util.List;
import java.util.Objects;

public class ForEachExpression extends ControlBlockAstNode {
    private final String label;
    private final List<AstNode> iterators;
    private final List<AstNode> values;
    private final AstNode body;

    ForEachExpression(Token startToken, String label, List<AstNode> iterators, List<AstNode> values, AstNode body) {
        super(startToken, iterators, values, body);
        this.label = label;
        this.iterators = iterators;
        this.values = values;
        this.body = body;
    }

    public String getLabel() {
        return label;
    }

    public List<AstNode> getIterators() {
        return iterators;
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
                Objects.equals(iterators, forEachExpression.iterators) &&
                Objects.equals(values, forEachExpression.values) &&
                Objects.equals(body, forEachExpression.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, iterators, values, body);
    }

    @Override
    public String toString() {
        return "ForEachExpression{" +
                "label=" + label +
                ", iterators='" + iterators + '\'' +
                ", values=" + values +
                ", body=" + body +
                '}';
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.LOOP;
    }
}
