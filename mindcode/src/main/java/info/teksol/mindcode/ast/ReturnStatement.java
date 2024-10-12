package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.generator.AstContextType;

import java.util.Objects;

public class ReturnStatement extends ControlBlockAstNode {
    private final AstNode retval;

    ReturnStatement(InputPosition inputPosition, AstNode expression) {
        super(inputPosition, expression);
        this.retval = expression;
    }

    public AstNode getRetval() {
        return retval;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ReturnStatement statement && Objects.equals(statement.retval, retval);
    }

    @Override
    public int hashCode() {
        return Objects.hash(retval);
    }

    @Override
    public String toString() {
        return "ReturnStatement{" + (retval == null ? "" : "fnRetVal='" + retval) + "'}";
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.RETURN;
    }
}
