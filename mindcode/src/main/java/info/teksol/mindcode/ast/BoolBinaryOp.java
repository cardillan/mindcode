package info.teksol.mindcode.ast;

public class BoolBinaryOp extends BinaryOp {

    public BoolBinaryOp(AstNode left, String op, AstNode right) {
        super(left, op, right);
    }

    public String toString() {
        return "BoolBinaryOp{" +
                "left=" + left +
                ", op='" + op + '\'' +
                ", right=" + right +
                '}';
    }
}
