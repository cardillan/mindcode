package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstBoolean(SourcePosition sourcePosition, boolean value) implements AstConfiguration {

    public AstBoolean(boolean value) {
        this(SourcePosition.EMPTY, value);
    }

    @Override
    public AstBoolean withEmptyPosition() {
        return new AstBoolean(SourcePosition.EMPTY, value);
    }
}
