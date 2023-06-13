package info.teksol.mindcode.ast;

import org.antlr.v4.runtime.Token;

public class BoolBinaryOp extends BinaryOp {

    public BoolBinaryOp(Token startToken, AstNode left, String op, AstNode right) {
        super(startToken, left, op, right);
    }

    public String toString() {
        return "BoolBinaryOp{" +
                "left=" + left +
                ", op='" + op + '\'' +
                ", right=" + right +
                '}';
    }
}
