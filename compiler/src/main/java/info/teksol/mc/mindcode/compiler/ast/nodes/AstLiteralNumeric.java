package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.mc.common.InputPosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AstLiteralNumeric extends AstLiteral {

    protected AstLiteralNumeric(InputPosition inputPosition, String literal) {
        super(inputPosition, literal);
        if (literal.isEmpty()) {
            throw new IllegalArgumentException("Value cannot be empty");
        }
    }
}