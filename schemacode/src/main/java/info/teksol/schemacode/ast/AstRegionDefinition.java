package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstRegionDefinition(SourcePosition sourcePosition, String name, AstSchemaRegion region) implements AstSchemaElement {

    public AstRegionDefinition(String name, AstSchemaRegion region) {
        this(SourcePosition.EMPTY, name, region);
    }

    @Override
    public AstRegionDefinition withEmptyPosition() {
        return new AstRegionDefinition(SourcePosition.EMPTY, name, erasePosition(region));
    }
}
