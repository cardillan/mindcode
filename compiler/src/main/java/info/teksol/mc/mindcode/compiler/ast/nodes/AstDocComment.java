package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@AstNode
public class AstDocComment extends AstDeclaration {
    private final String comment;

    public AstDocComment(SourcePosition sourcePosition, String comment) {
        super(sourcePosition);
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstDocComment that = (AstDocComment) o;
        return comment.equals(that.comment);
    }

    @Override
    public int hashCode() {
        return comment.hashCode();
    }

}
