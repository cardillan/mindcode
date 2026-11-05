package io.github.cardillan.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
@AstNode
public class AstEnhancedComment extends AstStatement {
    protected final List<AstExpression> parts;

    public AstEnhancedComment(SourcePosition sourcePosition, List<AstExpression> parts) {
        super(sourcePosition, children(parts));
        this.parts = List.copyOf(Objects.requireNonNull(parts));
    }

    @Override
    public AstNodeScope getScopeRestriction() {
        return AstNodeScope.NONE;
    }

    public List<AstExpression> getParts() {
        return parts;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstEnhancedComment that = (AstEnhancedComment) o;
        return Objects.equals(parts, that.parts);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parts);
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.CALL;
    }
}
