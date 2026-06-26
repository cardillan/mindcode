package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstSchemaBlock(SourcePosition sourcePosition, String type) implements AstSchemaElement {

    @Override
    public AstSchemaItem withEmptyPosition() {
        return new AstSchemaBlock(SourcePosition.EMPTY, type);
    }
}
