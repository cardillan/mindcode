package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.SourceFile;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseAstNode implements AstNode {
    private final Token startToken;
    private final SourceFile sourceFile;
    private final List<AstNode> children;

    protected BaseAstNode(Token startToken, SourceFile sourceFile) {
        this.startToken = startToken;
        this.sourceFile = sourceFile;
        this.children = List.of();
    }

    protected BaseAstNode(Token startToken, SourceFile sourceFile, AstNode... children) {
        this.startToken = startToken;
        this.sourceFile = sourceFile;
        this.children = List.of(children);
    }

    protected BaseAstNode(Token startToken, SourceFile sourceFile, List<? extends AstNode> children) {
        this.startToken = startToken;
        this.sourceFile = sourceFile;
        this.children = List.copyOf(children);
    }

    protected BaseAstNode(Token startToken, SourceFile sourceFile, List<? extends AstNode> children1, List<? extends AstNode> children2, AstNode... other) {
        this.startToken = startToken;
        this.sourceFile = sourceFile;
        List<AstNode> tmp = new ArrayList<>(children1);
        tmp.addAll(children2);
        tmp.addAll(Arrays.asList(other));
        this.children = List.copyOf(tmp);
    }

    protected BaseAstNode(Token startToken, SourceFile sourceFile, List<? extends AstNode> children, AstNode... other) {
        this.startToken = startToken;
        this.sourceFile = sourceFile;
        List<AstNode> tmp = new ArrayList<>(children);
        tmp.addAll(Arrays.asList(other));
        this.children = List.copyOf(tmp);
    }

    @Override
    public Token startToken() {
        return startToken;
    }

    public SourceFile sourceFile() {
        return sourceFile;
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
