package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstRgbaValue(SourcePosition sourcePosition, int red, int green, int blue, int alpha) implements AstColor {

    @Override
    public AstRgbaValue withEmptyPosition() {
        return new AstRgbaValue(SourcePosition.EMPTY, red, green, blue, alpha);
    }
}
