package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
@AstNode
public class AstMlogComment extends AstFragment {
    private final String whitespace;
    private final String comment;

    public AstMlogComment(SourcePosition sourcePosition, String whitespace, String comment) {
        super(sourcePosition);
        this.whitespace = Objects.requireNonNull(whitespace);
        this.comment = Objects.requireNonNull(comment);
    }

    public String getWhitespace() {
        return whitespace;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstMlogComment that = (AstMlogComment) o;
        return whitespace.equals(that.whitespace) && comment.equals(that.comment);
    }

    @Override
    public int hashCode() {
        int result = whitespace.hashCode();
        result = 31 * result + comment.hashCode();
        return result;
    }
}
