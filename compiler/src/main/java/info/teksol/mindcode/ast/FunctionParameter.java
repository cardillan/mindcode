package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;

import java.util.Objects;

public class FunctionParameter extends BaseAstNode {
    private final String name;
    private final boolean inModifier;
    private final boolean outModifier;

    public FunctionParameter(InputPosition inputPosition, String name, boolean inModifier, boolean outModifier) {
        super(inputPosition);
        this.name = Objects.requireNonNull(name);
        this.inModifier = inModifier;
        this.outModifier = outModifier;
    }

    public String getName() {
        return name;
    }

    public boolean isInModifier() {
        return inModifier;
    }

    public boolean isOutModifier() {
        return outModifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionParameter that = (FunctionParameter) o;
        return inModifier == that.inModifier && outModifier == that.outModifier && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + Boolean.hashCode(inModifier);
        result = 31 * result + Boolean.hashCode(outModifier);
        return result;
    }

    @Override
    public String toString() {
        return "FunctionParameter{" +
                "name='" + name + '\'' +
                ", inModifier=" + inModifier +
                ", outModifier=" + outModifier +
                '}';
    }
}
