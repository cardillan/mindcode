package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.InputPosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralBoolean extends AstLiteral {

    public AstLiteralBoolean(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
        if (!"true".equals(literal) && !"false".equals(literal)) {
            throw new IllegalArgumentException("Boolean literal is not 'true' or 'false'");
        }
    }

    public AstLiteralBoolean(InputPosition inputPosition, boolean value) {
        this(inputPosition, value ? "true" : "false");
    }

    public boolean getValue() {
        return "true".equals(getLiteral());
    }

    @Override
    public AstLiteralBoolean withInputPosition(InputPosition inputPosition) {
        return new AstLiteralBoolean(inputPosition, getValue());
    }

    @Override
    public double getDoubleValue() {
        return getLongValue();
    }

    @Override
    public long getLongValue() {
        return "true".equals(literal) ? 1 : 0;
    }
}
