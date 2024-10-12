package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.generator.AstContextType;

import java.util.Objects;

public class RangedForExpression extends ControlBlockAstNode {
    private final String label;
    private final AstNode variable;
    private final Range range;
    private final AstNode body;

    public RangedForExpression(InputPosition inputPosition, String label, AstNode variable, Range range, AstNode body) {
        super(inputPosition, variable, range, body);
        this.label = label;
        this.variable = variable;
        this.range = range;
        this.body = body;
    }

    public String getLabel() {
        return label;
    }

    public AstNode getVariable() {
        return variable;
    }

    public Range getRange() {
        return range;
    }

    public AstNode getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RangedForExpression that)) return false;
        return Objects.equals(getLabel(), that.getLabel())
                && Objects.equals(getVariable(), that.getVariable())
                && Objects.equals(getRange(), that.getRange())
                && Objects.equals(getBody(), that.getBody());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLabel(), getVariable(), getRange(), getBody());
    }

    @Override
    public String toString() {
        return "RangedForExpression{" +
                "label=" + label +
                ", variable=" + variable +
                ", range=" + range +
                ", body=" + body +
                '}';
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.LOOP;
    }
}
