package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputFile;
import org.antlr.v4.runtime.Token;

public class BoolBinaryOp extends BinaryOp {

    public BoolBinaryOp(Token startToken, InputFile inputFile, AstNode left, String op, AstNode right) {
        super(startToken, inputFile, left,op, right);
    }

    public String toString() {
        return "BoolBinaryOp{" +
                "left=" + left +
                ", op='" + op + '\'' +
                ", right=" + right +
                '}';
    }
}
