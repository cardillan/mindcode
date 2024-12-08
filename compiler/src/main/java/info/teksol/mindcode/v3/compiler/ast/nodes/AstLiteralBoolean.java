package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

public class AstLiteralBoolean extends AstLiteral {

    public AstLiteralBoolean(@NotNull InputPosition inputPosition, @NotNull String literal) {
        super(inputPosition, literal);
        if (!"true".equals(literal) && !"false".equals(literal)) {
            throw new IllegalArgumentException("Boolean literal is not 'true' or 'false'");
        }
    }
}
