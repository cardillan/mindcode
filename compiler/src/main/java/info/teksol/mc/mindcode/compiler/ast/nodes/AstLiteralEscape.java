package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralEscape extends AstLiteralString {

    public AstLiteralEscape(SourcePosition sourcePosition, String literal) {
        super(sourcePosition, literal);
    }

    @Override
    public AstLiteralEscape withSourcePosition(SourcePosition sourcePosition) {
        return new AstLiteralEscape(sourcePosition, literal);
    }
}
