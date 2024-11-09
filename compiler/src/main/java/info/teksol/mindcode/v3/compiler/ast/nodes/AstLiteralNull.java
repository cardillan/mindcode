package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;

public class AstLiteralNull extends AstLiteral {

    public AstLiteralNull(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
        if (!"null".equals(literal)) {
            throw new IllegalArgumentException("Null literal is not 'null'");
        }
    }
}
