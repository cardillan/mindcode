package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import org.jetbrains.annotations.NotNull;

public class AstLiteralString extends AstLiteral {

    public AstLiteralString(@NotNull InputPosition inputPosition, @NotNull String literal) {
        super(inputPosition, literal);
    }

}