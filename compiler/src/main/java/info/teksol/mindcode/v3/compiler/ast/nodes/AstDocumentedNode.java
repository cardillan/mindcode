package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class AstDocumentedNode extends AstBaseMindcodeNode {
    private final @Nullable AstDocComment docComment;

    public AstDocumentedNode(InputPosition inputPosition, @Nullable AstDocComment docComment) {
        super(inputPosition);
        this.docComment = docComment;
    }

    public @Nullable AstDocComment getDocComment() {
        return docComment;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstDocumentedNode that = (AstDocumentedNode) o;
        return Objects.equals(docComment, that.docComment);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(docComment);
    }
}
