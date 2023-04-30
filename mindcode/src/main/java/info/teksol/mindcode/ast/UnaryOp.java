package info.teksol.mindcode.ast;

import java.util.Objects;

public class UnaryOp extends BaseAstNode {
    private final String op;
    private final AstNode expression;

    UnaryOp(String op, AstNode expression) {
        super(expression);
        this.op = op;
        this.expression = expression;
    }

    public String getOp() {
        return op;
    }

    public AstNode getExpression() {
        return expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnaryOp unaryOp = (UnaryOp) o;
        return Objects.equals(op, unaryOp.op) &&
                Objects.equals(expression, unaryOp.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, expression);
    }

    @Override
    public String toString() {
        return "UnaryOp{" +
                "op='" + op + '\'' +
                ", expression=" + expression +
                '}';
    }
}
