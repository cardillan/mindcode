package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.mindustry.Position;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstDimensions(SourcePosition sourcePosition, int width, int height) implements AstSchemaItem {

    @Override
    public AstDimensions withEmptyPosition() {
        return new AstDimensions(SourcePosition.EMPTY, width, height);
    }

    public Position toPosition() {
        return new Position(width, height);
    }
}
