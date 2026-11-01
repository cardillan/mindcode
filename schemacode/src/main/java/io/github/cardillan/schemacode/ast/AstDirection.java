package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstDirection(SourcePosition sourcePosition, String direction) implements AstSchemaItem {

    public AstDirection(String direction) {
        this(SourcePosition.EMPTY, direction);
    }

    @Override
    public AstDirection withEmptyPosition() {
        return new AstDirection(SourcePosition.EMPTY, direction);
    }
}
