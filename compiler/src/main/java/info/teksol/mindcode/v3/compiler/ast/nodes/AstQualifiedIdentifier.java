package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AstQualifiedIdentifier extends AstBaseMindcodeNode {
    private final @NotNull List<@NotNull AstIdentifier> identifiers;
    private final @NotNull String fullName;

    public AstQualifiedIdentifier(InputPosition inputPosition, @NotNull List<@NotNull AstIdentifier> identifiers) {
        super(inputPosition);
        if (identifiers.isEmpty()) {
            throw new IllegalArgumentException("Empty identifiers");
        }
        this.identifiers = identifiers;
        this.fullName = identifiers.stream().map(AstIdentifier::getName).collect(Collectors.joining("."));
    }

    public AstQualifiedIdentifier(InputPosition inputPosition, @NotNull AstIdentifier... identifiers) {
        this(inputPosition, List.of(identifiers));
    }

    public AstQualifiedIdentifier(@NotNull AstIdentifier left, @NotNull AstIdentifier right) {
        this(left.inputPosition(), List.of(left, right));
    }

    public AstQualifiedIdentifier(@NotNull AstQualifiedIdentifier left, @NotNull AstIdentifier right) {
        this(left.inputPosition(), Stream.concat(left.identifiers.stream(), Stream.of(right)).toList());
    }

    public @NotNull String getFullName() {
        return fullName;
    }

    public @NotNull List<@NotNull AstIdentifier> getIdentifiers() {
        return identifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstQualifiedIdentifier that = (AstQualifiedIdentifier) o;
        return identifiers.equals(that.identifiers);
    }

    @Override
    public int hashCode() {
        return identifiers.hashCode();
    }

    @Override
    public String toString() {
        return "AstQualifiedIdentifier{" +
               "fullName=" + fullName +
               '}';
    }
}
