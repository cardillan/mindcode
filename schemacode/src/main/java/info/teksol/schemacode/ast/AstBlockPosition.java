package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record AstBlockPosition(SourcePosition sourcePosition, AstCoordinates anchor, BlockArray blockArrayType,
                               @Nullable AstCoordinates extension, boolean horizontal) implements AstSchemaItem {

    public AstBlockPosition(SourcePosition sourcePosition, AstCoordinates anchor) {
        this(sourcePosition, anchor, BlockArray.SINGLE, null, false);
    }

    @Override
    public AstBlockPosition withEmptyPosition() {
        return new AstBlockPosition(SourcePosition.EMPTY,
                erasePosition(anchor),
                blockArrayType,
                eraseNullablePosition(extension),
                horizontal);
    }

    public enum BlockArray {
        SINGLE, INCLUSIVE, EXCLUSIVE, AREA
    }
}
