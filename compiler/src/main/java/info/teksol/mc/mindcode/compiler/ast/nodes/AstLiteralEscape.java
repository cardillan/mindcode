package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.InputPosition;
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
