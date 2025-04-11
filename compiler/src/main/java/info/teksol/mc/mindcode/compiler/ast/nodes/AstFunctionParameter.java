package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@AstNode
public class AstFunctionParameter extends AstFragment {
    private final AstIdentifier identifier;
    private final boolean inModifier;
    private final boolean outModifier;
    private final boolean refModifier;
    private final boolean varargs;

    public AstFunctionParameter(SourcePosition sourcePosition, AstIdentifier identifier, boolean inModifier,
            boolean outModifier, boolean refModifier, boolean varargs) {
        super(sourcePosition, children(identifier));
        this.identifier = identifier;
        this.inModifier = inModifier;
        this.outModifier = outModifier;
        this.refModifier = refModifier;
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

    public boolean hasRefModifier() {
        return refModifier;
    }

    public boolean isVarargs() {
        return varargs;
    }

    public boolean isInput() {
        return !refModifier && (inModifier || !outModifier);
    }

    public boolean isOutput() {
        return outModifier;
    }

    public boolean isReference() {
        return refModifier;
    }

    public boolean isInputOutput() {
        return inModifier && outModifier;
    }

    public boolean isCompulsory() {
        return isInput();
    }

    public boolean isOptional() {
        return !isInput() && !isReference();
    }

    public int callSize() {
        return isReference() ? 0 : isInputOutput() ? 2 : 1;
    }

    public boolean matches(AstFunctionArgument argument) {
        if (!argument.hasExpression()) {
            return isOptional();
        } else if (argument.hasRefModifier()) {
            return isReference();
        } else if (argument.hasOutModifier()) {
            return argument.hasInModifier() ? isInputOutput() : isOutput();
        } else {
            // No ref or out modifier: must be input
            return isInput();
        }
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstFunctionParameter that = (AstFunctionParameter) o;
        return inModifier == that.inModifier && outModifier == that.outModifier && refModifier == that.refModifier
                && varargs == that.varargs && identifier.equals(that.identifier);
    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + Boolean.hashCode(inModifier);
        result = 31 * result + Boolean.hashCode(outModifier);
        result = 31 * result + Boolean.hashCode(refModifier);
        result = 31 * result + Boolean.hashCode(varargs);
        return result;
    }

}
