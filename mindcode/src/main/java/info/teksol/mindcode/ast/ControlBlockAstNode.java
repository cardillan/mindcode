package info.teksol.mindcode.ast;

import org.antlr.v4.runtime.Token;

import java.util.List;

// Base class for nodes that represent control blocks
public abstract class ControlBlockAstNode extends BaseAstNode {

    public ControlBlockAstNode(Token startToken) {
        super(startToken);
    }

    public ControlBlockAstNode(Token startToken, AstNode... children) {
        super(startToken, children);
    }

    public ControlBlockAstNode(Token startToken, List<? extends AstNode> children) {
        super(startToken, children);
    }

    public ControlBlockAstNode(Token startToken, List<? extends AstNode> children1, List<? extends AstNode> children2, AstNode... other) {
        super(startToken, children1, children2, other);
    }

    public ControlBlockAstNode(Token startToken, List<? extends AstNode> children, AstNode... other) {
        super(startToken, children, other);
    }
}
