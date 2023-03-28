package info.teksol.mindcode.ast;

public class NullLiteral extends ConstantAstNode {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof NullLiteral;
    }

    @Override
    public int hashCode() {
        return 65491987;
    }

    @Override
    public String getLiteral() {
        return "null";
    }

    @Override
    public String toString() {
        return "NullLiteral{}";
    }

    @Override
    public double getAsDouble() {
        return 0.0;
    }
}
