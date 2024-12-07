package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode(printFlat = true)
public class AstRequireFile extends AstRequire {
    private final AstLiteralString file;

    public AstRequireFile(SourcePosition sourcePosition, AstLiteralString file) {
        super(sourcePosition, file);
        this.file = Objects.requireNonNull(file);
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
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstRequireFile that = (AstRequireFile) o;
        return file.equals(that.file);
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }

}