package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record AstSchemaRegion(SourcePosition sourcePosition, List<AstBlock> blocks) implements AstSchemaElement {

    @Override
    public AstSchemaItem withEmptyPosition() {
        return new AstSchemaRegion(SourcePosition.EMPTY, erasePositions(blocks));
    }
}
