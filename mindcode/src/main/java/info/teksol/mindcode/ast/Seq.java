package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;

import java.util.Objects;

public class Seq extends BaseAstNode {
    private final AstNode rest;
    private final AstNode last;

    public Seq(InputPosition inputPosition, AstNode last) {
        super(inputPosition, last);
        this.rest = new NoOp();
        this.last = last;
    }

    public Seq(InputPosition inputPosition, AstNode rest, AstNode last) {
        super(inputPosition, rest, last);
        this.rest = rest;
        this.last = last;
    }

    public static Seq append(Seq first, Seq second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return new Seq(second.getInputPosition(), first, second);
        }
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
