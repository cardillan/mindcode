package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class AstLiteralDecimal extends AstLiteralNumeric {

    public AstLiteralDecimal(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
    }

}
