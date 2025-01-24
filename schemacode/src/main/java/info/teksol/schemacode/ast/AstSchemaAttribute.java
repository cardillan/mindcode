package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstSchemaAttribute(SourcePosition sourcePosition, String attribute, AstSchemaItem value) implements AstSchemaItem {

    @Override
    public AstSchemaAttribute withEmptyPosition() {
        return new AstSchemaAttribute(SourcePosition.EMPTY, attribute, erasePosition(value));
    }
}
