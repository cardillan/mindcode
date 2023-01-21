package info.teksol.mindcode.ast;

public class ContinueStatement implements AstNode {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof ContinueStatement;
    }

    @Override
    public int hashCode() {
        return 508113406;
    }

    @Override
    public String toString() {
        return "Continue{}";
    }
}
