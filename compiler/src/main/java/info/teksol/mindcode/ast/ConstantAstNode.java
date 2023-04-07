package info.teksol.mindcode.ast;

// Base class for nodes that represent constants
public abstract class ConstantAstNode extends BaseAstNode {

    protected ConstantAstNode() {
    }

    public abstract double getAsDouble();

    public abstract String getLiteral();
}
