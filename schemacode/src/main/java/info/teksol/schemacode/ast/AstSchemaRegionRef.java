package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstSchemaRegionRef(SourcePosition sourcePosition, String regionReference) implements AstSchemaElement {

    @Override
    public AstSchemaItem withEmptyPosition() {
        return new AstSchemaRegionRef(SourcePosition.EMPTY, regionReference);
    }
}
