package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.SourceFile;
import org.antlr.v4.runtime.Token;

public class BoolBinaryOp extends BinaryOp {

    public BoolBinaryOp(Token startToken, SourceFile sourceFile, AstNode left, String op, AstNode right) {
        super(startToken, sourceFile, left,op, right);
    }

    public String toString() {
        return "BoolBinaryOp{" +
                "left=" + left +
                ", op='" + op + '\'' +
                ", right=" + right +
                '}';
    }
}
