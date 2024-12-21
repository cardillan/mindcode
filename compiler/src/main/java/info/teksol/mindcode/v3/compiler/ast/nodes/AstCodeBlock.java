package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.generator.AstContextType;
import org.jspecify.annotations.NullMarked;

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
    public boolean equals(Object o) {
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
