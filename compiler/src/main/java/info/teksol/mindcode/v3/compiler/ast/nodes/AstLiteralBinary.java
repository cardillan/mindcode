package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralBinary extends AstLiteralNumeric {

    public AstLiteralBinary(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
        if (!literal.startsWith("0b")) {
            throw new IllegalArgumentException("Binary literal must start with '0b'");
        }
    }
}
