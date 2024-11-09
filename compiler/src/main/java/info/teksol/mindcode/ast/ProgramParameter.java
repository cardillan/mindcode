package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.generator.AstContextType;

import java.util.Objects;

public class ProgramParameter extends BaseAstNode {
    private final String name;
    private final AstNode value;

    public ProgramParameter(InputPosition inputPosition, String name, AstNode value) {
        super(inputPosition);
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
        ProgramParameter that = (ProgramParameter) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.ASSIGNMENT;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "name=" + name +
                ", value=" + value +
                '}';
    }
}
