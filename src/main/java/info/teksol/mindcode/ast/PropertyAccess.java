package info.teksol.mindcode.ast;

import java.util.Objects;

public class PropertyAccess implements AstNode {
    private final AstNode target;
    private final String property;

    public PropertyAccess(AstNode target, String property) {
        this.target = target;
        this.property = property;
    }

    public AstNode getTarget() {
        return target;
    }

    public String getProperty() {
        return property;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyAccess that = (PropertyAccess) o;
        return Objects.equals(target, that.target) &&
                Objects.equals(property, that.property);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, property);
    }

    @Override
    public String toString() {
        return "PropertyAccess{" +
                "target=" + target +
                ", property='" + property + '\'' +
                '}';
    }
}
