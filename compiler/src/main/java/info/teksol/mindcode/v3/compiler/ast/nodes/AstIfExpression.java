package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import info.teksol.util.CollectionUtils;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
@AstNode
public class AstIfExpression extends AstExpression {
    private final List<AstIfBranch> ifBranches;
    private final List<AstMindcodeNode> elseBranch;

    public AstIfExpression(InputPosition inputPosition, AstIfBranch firstBranch,
            List<AstIfBranch> ifBranches, List<AstMindcodeNode> elseBranch) {
        super(inputPosition, children(list(firstBranch), ifBranches, elseBranch));
        this.ifBranches = CollectionUtils.createList(firstBranch, ifBranches);
        this.elseBranch = elseBranch;
    }

    public AstIfExpression(InputPosition inputPosition, List<AstIfBranch> ifBranches, List<AstMindcodeNode> elseBranch) {
        super(inputPosition, children(ifBranches, elseBranch));
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
    public boolean equals(Object o) {
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

}
