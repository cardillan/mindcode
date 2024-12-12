package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public abstract class AstBaseMindcodeNode implements AstMindcodeNode {
    private final InputPosition inputPosition;
    protected final @Nullable AstDocComment docComment;

    protected AstBaseMindcodeNode(InputPosition inputPosition) {
        this.inputPosition = inputPosition;
        this.docComment = null;
    }

    protected AstBaseMindcodeNode(InputPosition inputPosition, @Nullable AstDocComment docComment) {
        this.inputPosition = inputPosition;
        this.docComment = docComment;
    }

    @Override
    public InputPosition inputPosition() {
        return inputPosition;
    }

    public @Nullable AstDocComment getDocComment() {
        return docComment;
    }

    public List<AstNode> getChildren() {
        return List.of();
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
