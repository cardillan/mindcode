package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.SourceFile;
import org.antlr.v4.runtime.Token;

import java.util.List;

// Base class for nodes that represent control blocks
public abstract class ControlBlockAstNode extends BaseAstNode {

    public ControlBlockAstNode(Token startToken, SourceFile sourceFile) {
        super(startToken, sourceFile);
    }

    public ControlBlockAstNode(Token startToken, SourceFile sourceFile, AstNode... children) {
        super(startToken, sourceFile, children);
    }

    public ControlBlockAstNode(Token startToken, SourceFile sourceFile, List<? extends AstNode> children) {
        super(startToken, sourceFile, children);
    }

    public ControlBlockAstNode(Token startToken, SourceFile sourceFile, List<? extends AstNode> children1, List<? extends AstNode> children2, AstNode... other) {
        super(startToken, sourceFile, children1, children2, other);
    }

    public ControlBlockAstNode(Token startToken, SourceFile sourceFile, List<? extends AstNode> children, AstNode... other) {
        super(startToken, sourceFile, children, other);
    }
}
