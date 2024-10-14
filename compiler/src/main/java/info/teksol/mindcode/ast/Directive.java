package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;

import java.util.List;
import java.util.Objects;

public class Directive extends BaseAstNode {
    private final DirectiveText option;
    private final List<DirectiveText> values;

    public Directive(InputPosition inputPosition, DirectiveText option, DirectiveText value) {
        super(inputPosition);
        this.option = option;
        this.values = List.of(value);
    }

    public Directive(InputPosition inputPosition, DirectiveText option, List<DirectiveText> values) {
        super(inputPosition);
        this.option = option;
        this.values = values;
    }

    public DirectiveText getOption() {
        return option;
    }

    public DirectiveText getValue() {
        return values.get(0);
    }

    public List<DirectiveText> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Directive that = (Directive) o;
        return Objects.equals(option, that.option) &&
                Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(option, values);
    }

    @Override
    public String toString() {
        return "Directive{" +
                "option='" + option +
                "', values='" + values +
                "'}";
    }
}
