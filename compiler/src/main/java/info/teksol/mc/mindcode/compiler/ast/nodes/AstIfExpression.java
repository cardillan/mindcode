package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.util.CollectionUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@AstNode
public class AstIfExpression extends AstExpression {
    private final List<AstIfBranch> ifBranches;
    private final List<AstMindcodeNode> elseBranch;

    public AstIfExpression(SourcePosition sourcePosition, AstIfBranch firstBranch,
            List<AstIfBranch> ifBranches, List<AstMindcodeNode> elseBranch) {
        super(sourcePosition, children(list(firstBranch), ifBranches, elseBranch));
        this.ifBranches = CollectionUtils.createList(firstBranch, ifBranches);
        this.elseBranch = elseBranch;
    }

    public AstIfExpression(SourcePosition sourcePosition, List<AstIfBranch> ifBranches, List<AstMindcodeNode> elseBranch) {
        super(sourcePosition, children(ifBranches, elseBranch));
        this.ifBranches = ifBranches;
        this.elseBranch = elseBranch;
    }

    public List<AstIfBranch> getIfBranches() {
        return ifBranches;
    }

    public List<AstMindcodeNode> getElseBranch() {
        return elseBranch;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstIfExpression that = (AstIfExpression) o;
        return ifBranches.equals(that.ifBranches) && elseBranch.equals(that.elseBranch);
    }

    @Override
    public int hashCode() {
        int result = ifBranches.hashCode();
        result = 31 * result + elseBranch.hashCode();
        return result;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.IF;
    }
}
