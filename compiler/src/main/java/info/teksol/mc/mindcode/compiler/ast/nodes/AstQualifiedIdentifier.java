package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.util.CollectionUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@NullMarked
@AstNode
public class AstQualifiedIdentifier extends AstExpression {
    private final List<AstIdentifier> identifiers;
    private final String fullName;

    public AstQualifiedIdentifier(SourcePosition sourcePosition, List<AstIdentifier> identifiers) {
        super(sourcePosition, children(identifiers));
        if (identifiers.isEmpty()) {
            throw new IllegalArgumentException("Empty identifiers");
        }
        this.identifiers = identifiers;
        this.fullName = identifiers.stream().map(AstIdentifier::getName).collect(Collectors.joining("."));
    }

    public AstQualifiedIdentifier(SourcePosition sourcePosition, AstIdentifier... identifiers) {
        this(sourcePosition, List.of(identifiers));
    }

    public AstQualifiedIdentifier(AstIdentifier left, AstIdentifier right) {
        this(left.sourcePosition(), List.of(left, right));
    }

    public AstQualifiedIdentifier(AstQualifiedIdentifier left, AstIdentifier right) {
        this(left.sourcePosition(), CollectionUtils.createList(left.identifiers, right));
    }

    public String getFullName() {
        return fullName;
    }

    public List<AstIdentifier> getIdentifiers() {
        return identifiers;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstQualifiedIdentifier that = (AstQualifiedIdentifier) o;
        return identifiers.equals(that.identifiers);
    }

    @Override
    public int hashCode() {
        return identifiers.hashCode();
    }

}
