package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.InputPosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
@AstNode
public class AstCodeBlock extends AstExpression {
    private final List<AstMindcodeNode> expressions;

    public AstCodeBlock(InputPosition inputPosition, List<AstMindcodeNode> expressions) {
        super(inputPosition, expressions);
        this.expressions = Objects.requireNonNull(expressions);
    }

    public List<AstMindcodeNode> getExpressions() {
        return expressions;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstCodeBlock that = (AstCodeBlock) o;
        return Objects.equals(expressions, that.expressions);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(expressions);
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.BODY;
    }
}