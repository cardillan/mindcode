package info.teksol.mindcode.ast;

public class NullLiteral extends BaseAstNode {
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
