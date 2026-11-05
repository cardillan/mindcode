package io.github.cardillan.mindcode.compiler.ast.nodes;

import io.github.cardillan.mindcode.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AstLiteralNumeric extends AstLiteral {

    protected AstLiteralNumeric(SourcePosition sourcePosition, String literal, boolean suppressWarning) {
        super(sourcePosition, literal, suppressWarning);
        if (literal.isEmpty()) {
            throw new IllegalArgumentException("Value cannot be empty");
        }
    }
}
