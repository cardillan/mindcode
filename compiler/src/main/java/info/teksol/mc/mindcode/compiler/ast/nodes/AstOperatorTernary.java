package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode
public class AstOperatorTernary extends AstExpression {
    private final AstExpression condition;
    private final AstMindcodeNode trueBranch;
    private final AstMindcodeNode falseBranch;

    public AstOperatorTernary(SourcePosition sourcePosition, AstExpression condition,
            AstMindcodeNode trueBranch, AstMindcodeNode falseBranch) {
        super(sourcePosition, children(condition, trueBranch, falseBranch));
        this.condition = Objects.requireNonNull(condition);
        this.trueBranch = Objects.requireNonNull(trueBranch);
        this.falseBranch = Objects.requireNonNull(falseBranch);
    }

    public AstExpression getCondition() {
        return condition;
    }

    public AstMindcodeNode getTrueBranch() {
        return trueBranch;
    }

    public AstMindcodeNode getFalseBranch() {
        return falseBranch;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstOperatorTernary that = (AstOperatorTernary) o;
        return condition.equals(that.condition) && trueBranch.equals(that.trueBranch) && falseBranch.equals(that.falseBranch);
    }

    @Override
    public int hashCode() {
        int result = condition.hashCode();
        result = 31 * result + trueBranch.hashCode();
        result = 31 * result + falseBranch.hashCode();
        return result;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.IF;
    }
}