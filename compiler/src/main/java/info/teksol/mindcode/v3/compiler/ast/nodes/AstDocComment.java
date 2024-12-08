package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

public class AstDocComment extends AstBaseMindcodeNode {
    private final @NotNull String comment;

    public AstDocComment(InputPosition inputPosition, @NotNull String comment) {
        super(inputPosition);
        this.comment = comment;
    }

    public @NotNull String getComment() {
        return comment;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstDocComment that = (AstDocComment) o;
        return comment.equals(that.comment);
    }

    @Override
    public int hashCode() {
        return comment.hashCode();
    }

    @Override
    public String toString() {
        return "AstDocComment{" +
               "comment='" + comment + '\'' +
               '}';
    }
}
