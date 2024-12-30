package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.InputPosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralNull extends AstLiteral {

    public AstLiteralNull(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
        if (!"null".equals(literal)) {
            throw new IllegalArgumentException("Null literal is not 'null'");
        }
    }

    public AstLiteralNull(InputPosition inputPosition) {
        this(inputPosition, "null");
    }

    @Override
    public AstLiteralNull withInputPosition(InputPosition inputPosition) {
        return new AstLiteralNull(inputPosition);
    }

    @Override
    public double getDoubleValue() {
        return 0.0;
    }

    @Override
    public long getLongValue() {
        return 0;
    }
}
