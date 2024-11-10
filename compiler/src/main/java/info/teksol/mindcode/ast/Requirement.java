package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;

import java.util.Objects;

public class Requirement extends BaseAstNode {
    private final String file;
    private final boolean library;

    public Requirement(InputPosition inputPosition, String file, boolean library) {
        super(inputPosition);
        this.file = Objects.requireNonNull(file);
        this.library = library;
    }

    public String getFile() {
        return file;
    }

    public boolean isLibrary() {
        return library;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Requirement requirement = (Requirement) o;
        return library == requirement.library && file.equals(requirement.file);
    }

    @Override
    public int hashCode() {
        int result = file.hashCode();
        result = 31 * result + Boolean.hashCode(library);
        return result;
    }

    @Override
    public String toString() {
        return "Require{" +
                "file='" + file + '\'' +
                ", library=" + library +
                '}';
    }
}
