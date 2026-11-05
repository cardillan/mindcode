package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mindcode.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstToken(SourcePosition sourcePosition, String tokenValue) implements AstSchemaItem {

    public AstToken(String tokenValue) {
        this(SourcePosition.EMPTY, tokenValue);
    }

    @Override
    public AstToken withEmptyPosition() {
        return new AstToken(SourcePosition.EMPTY, tokenValue);
    }
}
