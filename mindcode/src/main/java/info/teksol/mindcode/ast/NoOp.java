package info.teksol.mindcode.ast;

public class NoOp extends BaseAstNode {

    public NoOp() {
        super(null);
    }

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
