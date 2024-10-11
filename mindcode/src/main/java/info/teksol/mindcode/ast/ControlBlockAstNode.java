package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputFile;
import org.antlr.v4.runtime.Token;

import java.util.List;

// Base class for nodes that represent control blocks
public abstract class ControlBlockAstNode extends BaseAstNode {

    public ControlBlockAstNode(Token startToken, InputFile inputFile) {
        super(startToken, inputFile);
    }

    public ControlBlockAstNode(Token startToken, InputFile inputFile, AstNode... children) {
        super(startToken, inputFile, children);
    }

    public ControlBlockAstNode(Token startToken, InputFile inputFile, List<? extends AstNode> children) {
        super(startToken, inputFile, children);
    }

    public ControlBlockAstNode(Token startToken, InputFile inputFile, List<? extends AstNode> children1, List<? extends AstNode> children2, AstNode... other) {
        super(startToken, inputFile, children1, children2, other);
    }

    public ControlBlockAstNode(Token startToken, InputFile inputFile, List<? extends AstNode> children, AstNode... other) {
        super(startToken, inputFile, children, other);
    }
}
