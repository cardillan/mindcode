package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;

import java.util.Objects;

public class Requirement extends BaseAstNode {
    private final String file;
    private final boolean system;

    public Requirement(InputPosition inputPosition, String file, boolean system) {
        super(inputPosition);
        this.file = Objects.requireNonNull(file);
        this.system = system;
    }

    public String getFile() {
        return file;
    }

    public boolean isSystem() {
        return system;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Requirement requirement = (Requirement) o;
        return system == requirement.system && file.equals(requirement.file);
    }

    @Override
    public int hashCode() {
        int result = file.hashCode();
        result = 31 * result + Boolean.hashCode(system);
        return result;
    }

    @Override
    public String toString() {
        return "Require{" +
                "file='" + file + '\'' +
                ", system=" + system +
                '}';
    }
}
