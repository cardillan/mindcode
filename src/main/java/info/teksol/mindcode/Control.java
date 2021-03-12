package info.teksol.mindcode;

import java.util.Objects;

public class Control implements AstNode {
    private final String target;
    private final String property;
    private final AstNode value;

    public Control(String target, String property, AstNode value) {
        this.target = target;
        this.property = property;
        this.value = value;
    }

    public String getTarget() {
        return target;
    }

    public String getProperty() {
        return property;
    }

    public AstNode getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Control control = (Control) o;
        return Objects.equals(target, control.target) &&
                Objects.equals(property, control.property) &&
                Objects.equals(value, control.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, property, value);
    }

    @Override
    public String toString() {
        return "Control{" +
                "target='" + target + '\'' +
                ", property='" + property + '\'' +
                ", value=" + value +
                '}';
    }
}
