package info.teksol.mindcode.ast;

import java.util.Objects;

public class BinaryOp implements AstNode {
    private final AstNode left;
    private final String op;
    private final AstNode right;

    public BinaryOp(AstNode left, String op, AstNode right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public AstNode getLeft() {
        return left;
    }

    public String getOp() {
        return op;
    }

    public AstNode getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryOp binaryOp = (BinaryOp) o;
        return Objects.equals(left, binaryOp.left) &&
                Objects.equals(op, binaryOp.op) &&
                Objects.equals(right, binaryOp.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, op, right);
    }

    @Override
    public String toString() {
        return "BinaryOp{" +
                "left=" + left +
                ", op='" + op + '\'' +
                ", right=" + right +
                '}';
    }
}
