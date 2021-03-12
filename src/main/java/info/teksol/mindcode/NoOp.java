package info.teksol.mindcode;

public class NoOp implements AstNode {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof NoOp;
    }

    @Override
    public int hashCode() {
        return 723957202;
    }

    @Override
    public String toString() {
        return "NoOp{}";
    }
}
