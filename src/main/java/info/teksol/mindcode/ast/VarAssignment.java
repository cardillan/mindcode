package info.teksol.mindcode.ast;

import java.util.Objects;

public class VarAssignment implements AstNode {
    private final String varName;
    private final AstNode rvalue;

    public VarAssignment(String varName, AstNode rvalue) {
        this.varName = varName;
        this.rvalue = rvalue;
    }

    public String getVarName() {
        return varName;
    }

    public AstNode getRvalue() {
        return rvalue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VarAssignment that = (VarAssignment) o;
        return Objects.equals(varName, that.varName) &&
                Objects.equals(rvalue, that.rvalue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(varName, rvalue);
    }

    @Override
    public String toString() {
        return "VarAssignment{" +
                "varName=" + varName +
                ", rvalue=" + rvalue +
                '}';
    }
}