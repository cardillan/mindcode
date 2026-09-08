package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralString extends AstLiteral {

    public AstLiteralString(SourcePosition sourcePosition, String literal) {
        super(sourcePosition, literal, false);
        if (!literal.isEmpty() && literal.charAt(0) == '"') {
            throw new IllegalArgumentException("String literal value must not be enclosed in quotes.");
        }
    }

    public String getValue() {
        return literal;
    }

    @Override
    public AstLiteralString withSourcePosition(SourcePosition sourcePosition) {
        return new AstLiteralString(sourcePosition, literal);
    }

    @Override
    public double getDoubleValue() {
        return 1.0;
    }

    @Override
    public long getLongValue() {
        return 1;
    }
}
