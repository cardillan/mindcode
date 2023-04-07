package info.teksol.mindcode.ast;

import java.util.Objects;

public class Directive extends BaseAstNode {
    private final String option;
    private final String value;

    public Directive(String option, String value) {
        this.option = option;
        this.value = value;
    }

    public String getOption() {
        return option;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Directive that = (Directive) o;
        return Objects.equals(option, that.option) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(option, value);
    }

    @Override
    public String toString() {
        return "Directive{" +
                "option='" + option +
                "', value='" + value +
                "'}";
    }
}
