package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

@NullMarked
@AstNode(printFlat = true)
public class AstRequireFile extends AstRequire {
    private final AstLiteralString file;
    private final SortedSet<AstIdentifier> processors;

    public AstRequireFile(SourcePosition sourcePosition, AstLiteralString file, Collection<AstIdentifier> processors) {
        super(sourcePosition, file);
        this.file = Objects.requireNonNull(file);
        this.processors = new TreeSet<>(processors);
    }

    public AstLiteralString getFile() {
        return file;
    }

    @Override
    public boolean isLibrary() {
        return false;
    }

    @Override
    public String getName() {
        return file.getLiteral();
    }

    @Override
    public SortedSet<AstIdentifier> getProcessors() {
        return processors;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstRequireFile that = (AstRequireFile) o;
        return file.equals(that.file) && Objects.equals(processors, that.processors);
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }

}