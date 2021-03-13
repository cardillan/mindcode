package info.teksol.mindcode;

import java.util.Objects;

public class UnitAssignment implements AstNode {
    private final String name;
    private final AstNode value;

    public UnitAssignment(String name, AstNode value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public AstNode getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitAssignment that = (UnitAssignment) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return "UnitAssignment{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
