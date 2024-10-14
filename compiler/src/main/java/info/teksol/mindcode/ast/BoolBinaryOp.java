package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;

public class BoolBinaryOp extends BinaryOp {

    public BoolBinaryOp(InputPosition inputPosition, AstNode left, String op, AstNode right) {
        super(inputPosition, left,op, right);
    }

    public String toString() {
        return "BoolBinaryOp{" +
                "left=" + left +
                ", op='" + op + '\'' +
                ", right=" + right +
                '}';
    }
}
