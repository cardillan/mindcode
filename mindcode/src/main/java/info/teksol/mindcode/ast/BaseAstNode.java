package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseAstNode implements AstNode {
    private final List<AstNode> children;
    private final Token startToken;

    protected BaseAstNode(Token startToken) {
        this.startToken = startToken;
        this.children = List.of();
    }

    protected BaseAstNode(Token startToken, AstNode... children) {
        this.startToken = startToken;
        this.children = List.of(children);
    }

    protected BaseAstNode(Token startToken, List<? extends AstNode> children) {
        this.startToken = startToken;
        this.children = List.copyOf(children);
    }

    protected BaseAstNode(Token startToken, List<? extends AstNode> children, AstNode... other) {
        this.startToken = startToken;
        List<AstNode> tmp = new ArrayList<>(children);
        tmp.addAll(Arrays.asList(other));
        this.children = List.copyOf(tmp);
    }

    @Override
    public Token startToken() {
        return startToken;
    }

    @Override
    public List<AstNode> getChildren() {
        return children;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.NONE;
    }

    @Override
    public AstSubcontextType getSubcontextType() {
        return AstSubcontextType.BASIC;
    }
}
