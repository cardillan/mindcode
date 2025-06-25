package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@AstNode
public class AstMlogVariable extends AstFragment {
    private final AstIdentifier identifier;
    private final boolean inModifier;
    private final boolean outModifier;

    public AstMlogVariable(SourcePosition sourcePosition, AstIdentifier identifier, boolean inModifier, boolean outModifier) {
        super(sourcePosition, children(identifier));
        this.identifier = identifier;
        this.inModifier = inModifier;
        this.outModifier = outModifier;
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

    public boolean isInput() {
        return inModifier || !outModifier;
    }

    public boolean isOutput() {
        return outModifier;
    }

    public boolean isInputOutput() {
        return inModifier && outModifier;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstMlogVariable that = (AstMlogVariable) o;
        return inModifier == that.inModifier && outModifier == that.outModifier && identifier.equals(that.identifier);
    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + Boolean.hashCode(inModifier);
        result = 31 * result + Boolean.hashCode(outModifier);
        return result;
    }
}
