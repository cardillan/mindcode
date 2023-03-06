package info.teksol.mindcode.ast;

import java.util.Objects;

public class ReturnStatement extends BaseAstNode {
    private final AstNode retval;

    ReturnStatement(AstNode expression) {
        super(expression);
        this.retval = expression;
    }

    public AstNode getRetval() {
        return retval;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ReturnStatement && Objects.equals(((ReturnStatement)obj).retval, retval);
    }

    @Override
    public int hashCode() {
        return Objects.hash(retval);
    }

    @Override
    public String toString() {
        return "ReturnStatement{" + (retval == null ? "" : "retval='" + retval) + "'}";
    }
}
