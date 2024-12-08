package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

public class AstLiteralBinary extends AstLiteralNumeric {

    public AstLiteralBinary(@NotNull InputPosition inputPosition, @NotNull String literal) {
        super(inputPosition, literal);
        if (!literal.startsWith("0b")) {
            throw new IllegalArgumentException("Binary literal must start with '0b'");
        }
    }
}
