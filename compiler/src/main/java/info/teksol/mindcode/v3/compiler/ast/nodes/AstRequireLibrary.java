package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;

import java.util.Objects;

public class AstRequireLibrary extends AstBaseMindcodeNode {
    private final AstIdentifier library;

    public AstRequireLibrary(InputPosition inputPosition, AstIdentifier library) {
        super(inputPosition);
        this.library = Objects.requireNonNull(library);
    }

    public AstIdentifier getLibraryName() {
        return library;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstRequireLibrary that = (AstRequireLibrary) o;
        return library.equals(that.library);
    }

    @Override
    public int hashCode() {
        return library.hashCode();
    }

    @Override
    public String toString() {
        return "AstRequireLibrary{" +
                "library='" + library + '\'' +
                '}';
    }
}