package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

@NullMarked
@AstNode(printFlat = true)
public class AstRequireLibrary extends AstRequire {
    private final AstIdentifier library;
    private final SortedSet<AstIdentifier> processors;

    public AstRequireLibrary(SourcePosition sourcePosition, AstIdentifier library, List<AstIdentifier> processors) {
        super(sourcePosition, children(List.of(library), processors));
        this.library = Objects.requireNonNull(library);
        this.processors = new TreeSet<>(processors);
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
    public SortedSet<AstIdentifier> getProcessors() {
        return processors;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstRequireLibrary that = (AstRequireLibrary) o;
        return library.equals(that.library) && Objects.equals(processors, that.processors);
    }

    @Override
    public int hashCode() {
        return library.hashCode();
    }

}