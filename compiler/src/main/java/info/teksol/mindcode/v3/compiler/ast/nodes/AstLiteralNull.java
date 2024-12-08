package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

public class AstLiteralNull extends AstLiteral {

    public AstLiteralNull(@NotNull InputPosition inputPosition, @NotNull String literal) {
        super(inputPosition, literal);
        if (!"null".equals(literal)) {
            throw new IllegalArgumentException("Null literal is not 'null'");
        }
    }
}
