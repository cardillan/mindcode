package info.teksol.mindcode.ast;

public class BreakStatement implements AstNode {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof BreakStatement;
    }

    @Override
    public int hashCode() {
        return 480580631;
    }

    @Override
    public String toString() {
        return "Break{}";
    }
}
