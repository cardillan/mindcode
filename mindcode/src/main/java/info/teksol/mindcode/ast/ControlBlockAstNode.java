package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;

import java.util.List;

// Base class for nodes that represent control blocks
public abstract class ControlBlockAstNode extends BaseAstNode {

    public ControlBlockAstNode(InputPosition inputPosition) {
        super(inputPosition);
    }

    public ControlBlockAstNode(InputPosition inputPosition, AstNode... children) {
        super(inputPosition, children);
    }

    public ControlBlockAstNode(InputPosition inputPosition, List<? extends AstNode> children) {
        super(inputPosition, children);
    }

    public ControlBlockAstNode(InputPosition inputPosition, List<? extends AstNode> children1, List<? extends AstNode> children2, AstNode... other) {
        super(inputPosition, children1, children2, other);
    }

    public ControlBlockAstNode(InputPosition inputPosition, List<? extends AstNode> children, AstNode... other) {
        super(inputPosition, children, other);
    }
}
