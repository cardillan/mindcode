package info.teksol.mindcode.ast;

import info.teksol.mindcode.ParsingException;

import java.util.Objects;

public class VarAssignment implements AstNode {
    private final String varName;
    private final AstNode value;

    public VarAssignment(String varName, AstNode value) {
        if (RESERVED_KEYWORDS.contains(varName)) {
            throw new ParsingException(varName + " is a reserved keyword, please use a different word");
        }

        this.varName = varName;
        this.value = value;
    }

    public String getVarName() {
        return varName;
    }

    public AstNode getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VarAssignment that = (VarAssignment) o;
        return Objects.equals(varName, that.varName) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(varName, value);
    }

    @Override
    public String toString() {
        return "VarAssignment{" +
                "varName=" + varName +
                ", value=" + value +
                '}';
    }
}
