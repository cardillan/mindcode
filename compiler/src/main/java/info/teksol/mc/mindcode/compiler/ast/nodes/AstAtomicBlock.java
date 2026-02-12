package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
@AstNode
public class AstAtomicBlock extends AstLabeledExpression {
    private final List<AstMindcodeNode> expressions;
    private final boolean function;

    public AstAtomicBlock(SourcePosition sourcePosition, @Nullable AstIdentifier label, List<AstMindcodeNode> expressions,
            boolean function) {
        super(sourcePosition, label, expressions);
        this.expressions = Objects.requireNonNull(expressions);
        this.function = function;
    }

    @Override
    public AstNodeScope getScopeRestriction() {
        return AstNodeScope.NONE;
    }

    public List<AstMindcodeNode> getExpressions() {
        return expressions;
    }

    public boolean isFunction() {
        return function;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AstAtomicBlock that = (AstAtomicBlock) o;
        return Objects.equals(expressions, that.expressions) && function == that.function;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(expressions);
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.ATOMIC;
    }
}
