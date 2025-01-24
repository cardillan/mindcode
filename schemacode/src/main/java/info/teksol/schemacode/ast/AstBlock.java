package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public record AstBlock(SourcePosition sourcePosition, List<String>labels, String type, AstCoordinates position,
                       @Nullable AstDirection direction, @Nullable AstConfiguration configuration) implements AstSchemaItem {

    @Override
    public AstBlock withEmptyPosition() {
        return new AstBlock(SourcePosition.EMPTY, labels, type,
                erasePosition(position),
                eraseNullablePosition(direction),
                eraseNullablePosition(configuration));
    }

}
