package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;

public abstract class AstLiteralNumeric extends AstLiteral {

    public AstLiteralNumeric(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
        if (literal.isEmpty()) {
            throw new IllegalArgumentException("Value cannot be empty");
        }
    }

}
