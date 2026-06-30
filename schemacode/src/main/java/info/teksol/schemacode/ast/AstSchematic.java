package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record AstSchematic(SourcePosition sourcePosition, List<AstSchemaAttribute> attributes,
                           List<AstRegionDefinition> regions, List<AstBlock> blocks) implements AstDefinition {

    @Override
    public AstSchematic withEmptyPosition() {
        return new AstSchematic(SourcePosition.EMPTY,
                erasePositions(attributes),
                erasePositions(regions),
                erasePositions(blocks));
    }
}
