package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;

public class AstLiteralString extends AstLiteral {

    public AstLiteralString(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
    }

}