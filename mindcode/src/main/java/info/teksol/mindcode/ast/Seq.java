package info.teksol.mindcode.ast;

import java.util.Objects;

public class Seq extends BaseAstNode {
    private final AstNode rest;
    private final AstNode last;

    public Seq(AstNode last) {
        super(last);
        this.rest = new NoOp();
        this.last = last;
    }

    public Seq(AstNode rest, AstNode last) {
        super(rest, last);
        this.rest = rest;
        this.last = last;
    }

    public AstNode getRest() {
        return rest;
    }

    public AstNode getLast() {
        return last;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seq seq = (Seq) o;
        return Objects.equals(rest, seq.rest) &&
                Objects.equals(last, seq.last);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rest, last);
    }

    @Override
    public String toString() {
        return "Seq{" +
                "rest=" + rest +
                ", last=" + last +
                '}';
    }
}
