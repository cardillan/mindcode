package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@AstNode
public class AstFunctionParameter extends AstFragment {
    private final AstIdentifier identifier;
    private final boolean inModifier;
    private final boolean outModifier;
    private final boolean varargs;

    public AstFunctionParameter(InputPosition inputPosition, AstIdentifier identifier, boolean inModifier,
            boolean outModifier, boolean varargs) {
        super(inputPosition, children(identifier));
        this.identifier = identifier;
        this.inModifier = inModifier;
        this.outModifier = outModifier;
        this.varargs = varargs;
    }

    public AstIdentifier getIdentifier() {
        return identifier;
    }

    public String getName() {
        return identifier.getName();
    }

    public boolean hasInModifier() {
        return inModifier;
    }

    public boolean hasOutModifier() {
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

    public boolean matches(AstFunctionArgument argument) {
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
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstFunctionParameter that = (AstFunctionParameter) o;
        return inModifier == that.inModifier && outModifier == that.outModifier && varargs == that.varargs && identifier.equals(that.identifier);
    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + Boolean.hashCode(inModifier);
        result = 31 * result + Boolean.hashCode(outModifier);
        result = 31 * result + Boolean.hashCode(varargs);
        return result;
    }

}