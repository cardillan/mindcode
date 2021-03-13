package info.teksol.mindcode;

import java.util.Objects;

public class UnitRef implements AstNode {
    private final String name;

    public UnitRef(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitRef unitRef = (UnitRef) o;
        return Objects.equals(name, unitRef.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "UnitRef{" +
                "name='" + name + '\'' +
                '}';
    }
}
