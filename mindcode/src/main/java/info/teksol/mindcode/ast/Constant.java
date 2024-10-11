package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputFile;
import org.antlr.v4.runtime.Token;

import java.util.Objects;

public class Constant extends BaseAstNode {
    private final String name;
    private final AstNode value;

    public Constant(Token startToken, InputFile inputFile, String name, AstNode value) {
        super(startToken, inputFile);
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
        Constant that = (Constant) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return "Constant{" +
                "name=" + name +
                ", value=" + value +
                '}';
    }
}
