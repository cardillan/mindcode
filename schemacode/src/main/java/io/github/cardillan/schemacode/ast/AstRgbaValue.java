package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mindcode.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstRgbaValue(SourcePosition sourcePosition, int red, int green, int blue, int alpha) implements AstColor {

    public AstRgbaValue(int red, int green, int blue, int alpha) {
        this(SourcePosition.EMPTY, red, green, blue, alpha);
    }

    @Override
    public AstRgbaValue withEmptyPosition() {
        return new AstRgbaValue(SourcePosition.EMPTY, red, green, blue, alpha);
    }
}
