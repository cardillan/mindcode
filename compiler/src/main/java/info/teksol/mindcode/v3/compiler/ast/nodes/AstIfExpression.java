package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import info.teksol.util.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AstIfExpression extends AstExpression {
    private final @NotNull List<@NotNull AstIfBranch> ifBranches;
    private final @NotNull List<@NotNull AstMindcodeNode> elseBranch;

    public AstIfExpression(@NotNull InputPosition inputPosition, @NotNull AstIfBranch firstBranch,
            @NotNull List<@NotNull AstIfBranch> ifBranches, @NotNull List<@NotNull AstMindcodeNode> elseBranch) {
        super(inputPosition);
        this.ifBranches = CollectionUtils.createList(firstBranch, ifBranches);
        this.elseBranch = elseBranch;
    }

    public @NotNull List<@NotNull AstIfBranch> getIfBranches() {
        return ifBranches;
    }

    public @NotNull List<@NotNull AstMindcodeNode> getElseBranch() {
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

    @Override
    public String toString() {
        return "AstIfExpression{" +
               "ifBranches=" + ifBranches +
               ", elseBranch=" + elseBranch +
               '}';
    }
}
