package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;

import java.util.Objects;

public class Iterator extends BaseAstNode {
    private final boolean inModifier;
    private final boolean outModifier;
    private final VarRef varRef;

    public Iterator(InputPosition inputPosition, boolean inModifier, boolean outModifier, VarRef varRef) {
        super(inputPosition);
        this.inModifier = inModifier;
        this.outModifier = outModifier;
        this.varRef = varRef;
    }

    public boolean isInModifier() {
        return inModifier;
    }

    public boolean isOutModifier() {
        return outModifier;
    }

    public VarRef getVarRef() {
        return varRef;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Iterator it = (Iterator) o;
        return inModifier == it.inModifier && outModifier == it.outModifier && Objects.equals(varRef, it.varRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(varRef, inModifier, outModifier);
    }

    @Override
    public String toString() {
        return "Ref{" +
                "inModifier=" + inModifier +
                ", outModifier=" + outModifier +
                ", VarRef=" + varRef +
                '}';
    }

}
