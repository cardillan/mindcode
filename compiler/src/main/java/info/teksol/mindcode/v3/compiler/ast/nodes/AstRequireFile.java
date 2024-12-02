package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;

import java.util.Objects;

public class AstRequireFile extends AstBaseMindcodeNode {
    private final AstLiteralString file;

    public AstRequireFile(InputPosition inputPosition, AstLiteralString file) {
        super(inputPosition);
        this.file = Objects.requireNonNull(file);
    }

    public AstLiteralString getFileName() {
        return file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AstRequireFile that = (AstRequireFile) o;
        return file.equals(that.file);
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }

    @Override
    public String toString() {
        return "AstRequireFile{" +
                "file='" + file + '\'' +
                '}';
    }
}