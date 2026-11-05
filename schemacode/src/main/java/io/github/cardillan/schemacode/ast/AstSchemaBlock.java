package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mindcode.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstSchemaBlock(SourcePosition sourcePosition, String type) implements AstSchemaElement {

    public AstSchemaBlock(String type) {
        this(SourcePosition.EMPTY, type);
    }

    @Override
    public AstSchemaItem withEmptyPosition() {
        return new AstSchemaBlock(SourcePosition.EMPTY, type);
    }
}
