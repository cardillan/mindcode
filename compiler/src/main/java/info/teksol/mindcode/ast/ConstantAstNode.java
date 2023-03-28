package info.teksol.mindcode.ast;

// Base class for nodes that represent constants
public abstract class ConstantAstNode extends BaseAstNode implements ConstantExpression {

    protected ConstantAstNode() {
    }

    public abstract String getLiteral();
}
