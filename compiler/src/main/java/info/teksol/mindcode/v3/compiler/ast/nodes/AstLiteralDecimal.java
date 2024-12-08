package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

public class AstLiteralDecimal extends AstLiteralNumeric {

    public AstLiteralDecimal(@NotNull InputPosition inputPosition, @NotNull String literal) {
        super(inputPosition, literal);
    }

}
