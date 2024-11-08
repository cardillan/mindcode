package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;

public class AstLiteralBinary extends AstLiteralNumeric {

    public AstLiteralBinary(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
        if (!literal.startsWith("0b")) {
            throw new IllegalArgumentException("Binary literal must start with '0b'");
        }
    }
}
