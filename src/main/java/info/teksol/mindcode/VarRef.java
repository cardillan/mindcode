package info.teksol.mindcode;

import java.util.Objects;

public class VarRef implements AstNode {
    private final String name;

    public VarRef(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VarRef varRef = (VarRef) o;
        return Objects.equals(name, varRef.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "VarRef{" +
                "name='" + name + '\'' +
                '}';
    }
}
