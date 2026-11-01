package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstVirtual(SourcePosition sourcePosition) implements AstConfiguration {

    public AstVirtual() {
        this(SourcePosition.EMPTY);
    }

    @Override
    public AstVirtual withEmptyPosition() {
        return new AstVirtual(SourcePosition.EMPTY);
    }
}
