package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AstBaseMindcodeNode implements AstMindcodeNode {
    private final @NotNull InputPosition inputPosition;
    protected final @Nullable AstDocComment docComment;

    protected AstBaseMindcodeNode(@NotNull InputPosition inputPosition) {
        this.inputPosition = inputPosition;
        this.docComment = null;
    }

    protected AstBaseMindcodeNode(@NotNull InputPosition inputPosition, @Nullable AstDocComment docComment) {
        this.inputPosition = inputPosition;
        this.docComment = docComment;
    }

    @Override
    public @NotNull InputPosition inputPosition() {
        return inputPosition;
    }

    public @Nullable AstDocComment getDocComment() {
        return docComment;
    }

    public @NotNull List<@NotNull AstNode> getChildren() {
        return List.of();
    }

    @Override
    public @NotNull AstContextType getContextType() {
        return AstContextType.NONE;
    }

    @Override
    public @NotNull AstSubcontextType getSubcontextType() {
        return AstSubcontextType.BASIC;
    }
}
