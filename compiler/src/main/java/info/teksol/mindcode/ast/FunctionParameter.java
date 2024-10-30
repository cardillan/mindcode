package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;

import java.util.Objects;

public class FunctionParameter extends BaseAstNode {
    private final String name;
    private final boolean inModifier;
    private final boolean outModifier;
    private final boolean varArgs;

    public FunctionParameter(InputPosition inputPosition, String name, boolean inModifier, boolean outModifier, boolean varArgs) {
        super(inputPosition);
        this.name = Objects.requireNonNull(name);
        this.inModifier = inModifier;
        this.outModifier = outModifier;
        this.varArgs = varArgs;
    }

    public String getName() {
        return name;
    }

    public boolean hasInModifier() {
        return inModifier;
    }

    public boolean hasOutModifier() {
        return outModifier;
    }

    public boolean isInput() {
        return inModifier || !outModifier;
    }

    public boolean isOutput() {
        return outModifier;
    }

    public boolean isInputOutput() {
        return inModifier && outModifier;
    }

    public boolean isCompulsory() {
        return isInput();
    }

    public boolean isOptional() {
        return !isInput();
    }

    public boolean isVarArgs() {
        return varArgs;
    }

    public boolean matches(FunctionArgument argument) {
        if (!argument.hasExpression()) {
            return isOptional();
        } else if (argument.hasOutModifier()) {
            return argument.hasInModifier() ? isInputOutput() : isOutput();
        } else {
            // No out modifier: must be input
            return isInput();
        }
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
                ", varArgs=" + varArgs +
                '}';
    }
}
