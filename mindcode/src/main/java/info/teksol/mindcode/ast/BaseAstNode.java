package info.teksol.mindcode.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseAstNode implements AstNode {
    private final List<AstNode> children;

    protected BaseAstNode() {
        this.children = List.of();
    }

    protected BaseAstNode(AstNode... children) {
        this.children = List.of(children);
    }

    protected BaseAstNode(List<? extends AstNode> children) {
        this.children = List.copyOf(children);
    }

    protected BaseAstNode(List<? extends AstNode> children, AstNode... other) {
        List<AstNode> tmp = new ArrayList<>(children);
        tmp.addAll(Arrays.asList(other));
        this.children = List.copyOf(tmp);
    }

    @Override
    public List<AstNode> getChildren() {
        return children;
    }
}
