package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mindcode.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public record AstSchemaRegion(SourcePosition sourcePosition, @Nullable AstDimensions dimensions, List<AstBlock> blocks) implements AstSchemaElement {

    public AstSchemaRegion(@Nullable AstDimensions dimensions, List<AstBlock> blocks) {
        this(SourcePosition.EMPTY, dimensions, blocks);
    }

    @Override
    public AstSchemaItem withEmptyPosition() {
        return new AstSchemaRegion(SourcePosition.EMPTY, eraseNullablePosition(dimensions), erasePositions(blocks));
    }
}
