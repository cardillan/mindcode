package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.schemacode.mindustry.Position;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstDimensions(SourcePosition sourcePosition, int width, int height) implements AstSchemaItem {

    public AstDimensions(int width, int height) {
        this(SourcePosition.EMPTY, width, height);
    }

    @Override
    public AstDimensions withEmptyPosition() {
        return new AstDimensions(SourcePosition.EMPTY, width, height);
    }

    public Position toPosition() {
        return new Position(width, height);
    }
}
