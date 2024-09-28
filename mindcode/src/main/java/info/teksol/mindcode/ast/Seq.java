package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.SourceFile;
import org.antlr.v4.runtime.Token;

import java.util.Objects;

public class Seq extends BaseAstNode {
    private final AstNode rest;
    private final AstNode last;

    public Seq(Token startToken, SourceFile sourceFile, AstNode last) {
        super(startToken, sourceFile, last);
        this.rest = new NoOp();
        this.last = last;
    }

    public Seq(Token startToken, SourceFile sourceFile, AstNode rest, AstNode last) {
        super(startToken, sourceFile, rest, last);
        this.rest = rest;
        this.last = last;
    }

    public static Seq append(Seq current, Seq next) {
        if (current == null) {
            return next;
        } else {
            return new Seq(next.startToken(), current.sourceFile(), current, next);
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
