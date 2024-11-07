package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;

public class AstLiteralEscape extends AstLiteralString {

    public AstLiteralEscape(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
    }

    @Override
    public String toString() {
        return "AstLiteralEscape{" +
                "literal='" + literal + '\'' +
                '}';
    }
}
