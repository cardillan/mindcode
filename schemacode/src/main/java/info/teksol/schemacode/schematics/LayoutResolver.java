package info.teksol.schemacode.schematics;

import info.teksol.mc.util.MutableInteger;
import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.ast.AstBlock;
import info.teksol.schemacode.ast.AstSchemaBlock;
import info.teksol.schemacode.ast.AstSchemaRegion;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schematics.SchematicsBuilder.ResolverContext;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NullMarked
public class LayoutResolver {
    public static final char LABEL_ARRAY_CHAR = '#';

    private final SchematicsBuilder builder;

    private final Map<String, SchematicElement> indexedBlocks = new HashMap<>();

    private final Map<String, MutableInteger> arrayLabels = new HashMap<>();

    private int blockIndex = 0;

    public LayoutResolver(SchematicsBuilder builder) {
        this.builder = builder;
    }

    public SchematicElement resolve(List<AstBlock> blocks) {
        return createRegion(null, null, blocks);
    }

    private SchematicElement createBlock(@Nullable SchematicElement parent, AstBlock definition, AstSchemaBlock block) {
        return SchematicElement.createBlock(parent, definition, block.type(), blockIndex++);
    }

    private SchematicElement createRegion(@Nullable SchematicElement parent, @Nullable AstBlock definition, List<AstBlock> blocks) {
        SchematicElement region = SchematicElement.createRegion(parent, definition, blockIndex++);
        SchematicElement last = null;

        // Create the basic structure
        // TODO: no arrays yet
        for (AstBlock block : blocks) {
            SchematicElement element = convert(region, block);
            region.addElement(element);
            block.labels().forEach(label -> region.addLabel(label, element));
            element.setAnchor(last);
            last = element;
        }

        // Region structure complete. Resolve positions/dimensions
        ResolverContext context = builder.trackingResolverContext();
        int width = 0, height = 0;
        for (SchematicElement element : region.elements) {
            Position position = element.resolvePosition(context);
            width = Math.max(width, position.x() + element.dimensions().x());
            height = Math.max(height, position.y() + element.dimensions().y());
            element.updateOrigin();
        }

        region.setDimensions(new Position(width, height));
        return region;
    }

    private SchematicElement convert(SchematicElement parent, AstBlock definition) {
        return switch (definition.element()) {
            case AstSchemaBlock b -> createBlock(parent, definition, b);
            case AstSchemaRegion r -> createRegion(parent, definition, r.blocks());
            //case AstSchemaRegionRef r -> resolve(List.of());
            default -> throw new SchematicsInternalError("Unexpected block type: %s", definition.element());
        };
    }
}
