package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class AstLiteralFloat extends AstLiteralNumeric {

    public AstLiteralFloat(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
    }

}
