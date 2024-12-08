package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

public abstract class AstLiteralNumeric extends AstLiteral {

    protected AstLiteralNumeric(@NotNull InputPosition inputPosition, @NotNull String literal) {
        super(inputPosition, literal);
        if (literal.isEmpty()) {
            throw new IllegalArgumentException("Value cannot be empty");
        }
    }

}
