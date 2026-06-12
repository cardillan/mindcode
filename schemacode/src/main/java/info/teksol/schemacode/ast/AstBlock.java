package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public record AstBlock(SourcePosition sourcePosition, List<String> labels, String type, AstBlockPosition position,
                       @Nullable AstDirection direction, @Nullable AstConfiguration configuration) implements AstSchemaItem {

    public AstBlock(SourcePosition sourcePosition, List<String> labels, String type, AstCoordinates anchor,
            @Nullable AstDirection direction, @Nullable AstConfiguration configuration) {
        this(sourcePosition, labels, type, new AstBlockPosition(anchor.sourcePosition(), anchor), direction, configuration);
    }

    public AstCoordinates anchor() {
        return position.anchor();
    }

    @Override
    public AstBlock withEmptyPosition() {
        return new AstBlock(SourcePosition.EMPTY, labels, type,
                erasePosition(position),
                eraseNullablePosition(direction),
                eraseNullablePosition(configuration));
    }
}
