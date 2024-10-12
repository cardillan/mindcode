package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;

public class NoOp extends BaseAstNode {

    public NoOp() {
        super(InputPosition.EMPTY);
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

    public static NoOp NO_OP = new NoOp();
}
