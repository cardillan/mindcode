package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

public class AstLiteralHexadecimal extends AstLiteralNumeric {

    public AstLiteralHexadecimal(@NotNull InputPosition inputPosition, @NotNull String literal) {
        super(inputPosition, literal);
        if (!literal.startsWith("0x")) {
            throw new IllegalArgumentException("Binary literal must start with '0x'");
        }
    }

}
