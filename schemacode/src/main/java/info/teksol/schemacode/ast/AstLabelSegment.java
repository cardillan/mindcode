package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstLabelSegment(SourcePosition sourcePosition, String name) implements AstSchemaItem {

    public AstLabelSegment(String name) {
        this(SourcePosition.EMPTY, name);
    }

    @Override
    public AstLabelSegment withEmptyPosition() {
        return new AstLabelSegment(SourcePosition.EMPTY, name);
    }
}
