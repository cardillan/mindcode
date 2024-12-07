package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstRequireLibrary extends AstRequire {
    private final AstIdentifier library;

    public AstRequireLibrary(SourcePosition sourcePosition, AstIdentifier library) {
        super(sourcePosition, library);
        this.library = Objects.requireNonNull(library);
    }

    public AstIdentifier getLibrary() {
        return library;
    }

    @Override
    public boolean isLibrary() {
        return true;
    }

    @Override
    public String getName() {
        return library.getName();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstRequireLibrary that = (AstRequireLibrary) o;
        return library.equals(that.library);
    }

    @Override
    public int hashCode() {
        return library.hashCode();
    }

}