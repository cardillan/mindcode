package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralEscape extends AstLiteralString {

    public AstLiteralEscape(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
    }

    @Override
    public AstLiteralEscape withInputPosition(InputPosition inputPosition) {
        return new AstLiteralEscape(inputPosition, literal);
    }
}
