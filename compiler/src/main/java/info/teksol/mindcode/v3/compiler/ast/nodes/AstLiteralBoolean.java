package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
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
}
