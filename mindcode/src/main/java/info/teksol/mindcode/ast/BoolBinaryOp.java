package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.instructions.AstContextType;
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

    @Override
    public AstContextType getContextType() {
        return AstContextType.BOOL_BINARY_OP;
    }
}
