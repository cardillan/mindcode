package info.teksol.mindcode;

public class NullLiteral implements AstNode {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof NullLiteral;
    }

    @Override
    public int hashCode() {
        return 65491987;
    }

    @Override
    public String toString() {
        return "NullLiteral{}";
    }
}
