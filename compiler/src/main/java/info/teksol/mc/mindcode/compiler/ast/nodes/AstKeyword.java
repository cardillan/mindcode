package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstKeyword extends AstExpression {
    private final String keyword;

    public AstKeyword(SourcePosition sourcePosition, String keyword) {
        super(sourcePosition);
        this.keyword = Objects.requireNonNull(keyword);
        if (keyword.charAt(0) != ':') {
            throw new IllegalArgumentException("Keyword must start with :");
        }
    }

    public String getKeyword() {
        return keyword.substring(1);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstKeyword that = (AstKeyword) o;
        return keyword.equals(that.keyword);
    }

    @Override
    public int hashCode() {
        return keyword.hashCode();
    }

}