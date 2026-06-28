package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstLabelSegment(SourcePosition sourcePosition, String name) implements AstSchemaItem {
    @Override
    public AstLabelSegment withEmptyPosition() {
        return new AstLabelSegment(SourcePosition.EMPTY, name);
    }

    public static AstLabelSegment of(SourcePosition sourcePosition, String name) {
        return new AstLabelSegment(sourcePosition, name);
    }
}
