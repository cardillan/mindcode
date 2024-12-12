package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AstNode(printFlat = true)
public class AstLiteralString extends AstLiteral {

    public AstLiteralString(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
    }

}