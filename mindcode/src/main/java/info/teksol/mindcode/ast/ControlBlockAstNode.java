package info.teksol.mindcode.ast;

import java.util.List;

// Base class for nodes that represent control blocks
public abstract class ControlBlockAstNode extends BaseAstNode {

    protected ControlBlockAstNode() {
    }

    protected ControlBlockAstNode(AstNode... children) {
        super(children);
    }

    protected ControlBlockAstNode(List<? extends AstNode> children) {
        super(children);
    }

    protected ControlBlockAstNode(List<? extends AstNode> children, AstNode... other) {
        super(children, other);
    }
}
