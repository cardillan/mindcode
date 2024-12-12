package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class AstFunctionParameter extends AstFragment {
    private final AstIdentifier name;
    private final boolean inModifier;
    private final boolean outModifier;
    private final boolean varargs;

    public AstFunctionParameter(InputPosition inputPosition, AstIdentifier name, boolean inModifier,
            boolean outModifier, boolean varargs) {
        super(inputPosition);
        this.name = name;
        this.inModifier = inModifier;
        this.outModifier = outModifier;
        this.varargs = varargs;
    }

    public AstIdentifier getName() {
        return name;
    }

    public boolean isInModifier() {
        return inModifier;
    }

    public boolean isOutModifier() {
        return outModifier;
    }

    public boolean isVarargs() {
        return varargs;
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

    public int callSize() {
        return isInputOutput() ? 2 : 1;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstFunctionParameter that = (AstFunctionParameter) o;
        return inModifier == that.inModifier && outModifier == that.outModifier && varargs == that.varargs && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + Boolean.hashCode(inModifier);
        result = 31 * result + Boolean.hashCode(outModifier);
        result = 31 * result + Boolean.hashCode(varargs);
        return result;
    }

    @Override
    public String toString() {
        return "AstFunctionParameter{" +
               "name=" + name +
               ", inModifier=" + inModifier +
               ", outModifier=" + outModifier +
               ", varargs=" + varargs +
               '}';
    }
}
