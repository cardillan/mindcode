package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.SourceFile;
import info.teksol.mindcode.compiler.generator.AstContextType;
import org.antlr.v4.runtime.Token;

import java.util.List;
import java.util.Objects;

public class BinaryOp extends BaseAstNode {
    protected final AstNode left;
    protected final String op;
    protected final AstNode right;

    public BinaryOp(Token startToken, SourceFile sourceFile, AstNode left, String op, AstNode right) {
        super(startToken, sourceFile, left, right);
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
    public List<AstNode> getChildren() {
        return List.of(left, right);
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

    @Override
    public AstContextType getContextType() {
        return AstContextType.OPERATOR;
    }
}
