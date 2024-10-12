package info.teksol.mindcode.ast;


import info.teksol.mindcode.InputPosition;

import java.util.Objects;

public class VarRef extends BaseAstNode {
    private final String name;

    public VarRef(InputPosition inputPosition, String name) {
        super(inputPosition);
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
