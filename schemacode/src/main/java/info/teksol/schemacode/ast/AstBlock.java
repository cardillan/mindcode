package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public record AstBlock(SourcePosition sourcePosition, List<String> labels, AstSchemaElement element, AstBlockPosition position,
                       @Nullable AstDirection direction, @Nullable AstConfiguration configuration) implements AstSchemaItem {

    public AstBlock(SourcePosition sourcePosition, List<String> labels, AstSchemaElement element, AstCoordinates anchor,
            @Nullable AstDirection direction, @Nullable AstConfiguration configuration) {
        this(sourcePosition, labels, element, new AstBlockPosition(anchor.sourcePosition(), anchor), direction, configuration);
    }

    public AstCoordinates anchor() {
        return position.anchor();
    }

    public boolean isCluster() {
        return !(element instanceof AstSchemaBlock);
    }

    public String type() {
        if (element instanceof AstSchemaBlock block) return block.type();
        throw new MindcodeInternalError("Type not available for a compound block.");
    }

    @Override
    public AstBlock withEmptyPosition() {
        return new AstBlock(SourcePosition.EMPTY, labels,
                erasePosition(element),
                erasePosition(position),
                eraseNullablePosition(direction),
                eraseNullablePosition(configuration));
    }
}
